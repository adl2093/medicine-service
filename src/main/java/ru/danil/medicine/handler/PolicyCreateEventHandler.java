package ru.danil.medicine.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.messaging.DlqProducer;
import ru.danil.medicine.service.IdempotencyKeyService;
import ru.danil.medicine.service.PolicyWithIdempotency;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyCreateEventHandler {
    private final PolicyWithIdempotency policyWithIdempotency;
    private final IdempotencyKeyService idempotencyKeyService;
    private final DlqProducer dlqProducer;

    @KafkaListener(topics = "policy-created-topic")
    public void handle(List<RetryableTaskDTO> retryableTaskDTOs, Acknowledgment ack) {
        List<UUID> ids = retryableTaskDTOs.stream().map(RetryableTaskDTO::getId).toList();
        Set<UUID> processedIds = new HashSet<>(idempotencyKeyService.findByIdIn(ids));
        List<RetryableTaskDTO> newTasks = filterNewTask(retryableTaskDTOs, processedIds);
        log.info("Новых сообщений для обработки: {}, уже обработано: {}", newTasks.size(), processedIds.size());

        List<RetryableTaskDTO> failedTasks = policyWithIdempotency.processTasks(newTasks);
        if (!failedTasks.isEmpty()) {
            dlqProducer.sendRetryableTasksToCreateDQLTopic(failedTasks);
        }
        ack.acknowledge();
        log.info("Пачка обработана, комитим");
    }

    private List<RetryableTaskDTO> filterNewTask(List<RetryableTaskDTO> retryableTaskDTOs, Set<UUID> processedIds) {
        return retryableTaskDTOs.stream()
                .filter(retryableTaskDTO -> !processedIds.contains(retryableTaskDTO.getId()))
                .toList();
    }
}