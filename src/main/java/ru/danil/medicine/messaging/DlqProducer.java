package ru.danil.medicine.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.danil.medicine.dto.RetryableTaskDTO;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DlqProducer {
    private final KafkaTemplate<UUID, RetryableTaskDTO> kafkaTemplate;

    public void sendRetryableTasksToCreateDQLTopic(List<RetryableTaskDTO> retryableTaskDTOs) {
        retryableTaskDTOs.forEach(dto -> {
            kafkaTemplate.send("${kafka-topic.policy-created-dlq-name}", dto.getId(), dto);
            log.info("Событие {} отправлено в DLQ", dto.getId());
        });
        kafkaTemplate.flush();
    }
}
