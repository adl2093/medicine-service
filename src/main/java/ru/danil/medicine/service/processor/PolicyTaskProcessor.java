package ru.danil.medicine.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danil.medicine.config.DlqProducer;
import ru.danil.medicine.dto.PolicyDTO;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.mapper.RetryableTaskMapper;
import ru.danil.medicine.service.IdempotencyKeyService;
import ru.danil.medicine.service.PolicyService;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class PolicyTaskProcessor {
    private final IdempotencyKeyService idempotencyKeyService;
    private final DlqProducer dlqProducer;
    private final RetryableTaskMapper retryableTaskMapper;
    private final PolicyService policyService;

    @Transactional
    public void process(List<RetryableTaskDTO> retryableTaskDTOs){
        List<UUID> ids = retryableTaskDTOs.stream().map(RetryableTaskDTO::getId).toList();
        Set<UUID> processedIds = new HashSet<>(idempotencyKeyService.findByIdIn(ids));
        List<RetryableTaskDTO> newTasks = filterNewTask(retryableTaskDTOs, processedIds);
        log.info("Новых сообщений для обработки: {}, уже обработано: {}", newTasks.size(), processedIds.size());

        List<RetryableTaskDTO> failedTasks = processTasks(newTasks);
        if (!failedTasks.isEmpty()) {
            dlqProducer.sendDLQ(failedTasks);
        }
    }

    private List<RetryableTaskDTO> filterNewTask(List<RetryableTaskDTO> retryableTaskDTOs, Set<UUID> processedIds) {
        return retryableTaskDTOs.stream()
                .filter(retryableTaskDTO -> !processedIds.contains(retryableTaskDTO.getId()))
                .toList();
    }

    public List<RetryableTaskDTO> processTasks(List<RetryableTaskDTO> newTasks) {
        List<RetryableTaskDTO> failedTasks = new ArrayList<>();
        for (RetryableTaskDTO retryableTaskDTO : newTasks) {
            if (!processSingleTask(retryableTaskDTO)) {
                failedTasks.add(retryableTaskDTO);
            }
        }
        saveIdempotencyKey(newTasks);
        return failedTasks;
    }

    private void saveIdempotencyKey(List<RetryableTaskDTO> newTasks) {
        List<UUID> newIds = newTasks.stream()
                .map(RetryableTaskDTO::getId)
                .toList();
        if (!newIds.isEmpty()) {
            idempotencyKeyService.insertBatch(newIds);
            log.info("Сохранено {} новых идемпотентных ключей", newIds.size());
        }
    }

    private boolean processSingleTask(RetryableTaskDTO dto) {
        try {
            PolicyDTO policyDTO = retryableTaskMapper.toPolicyDTOFromPayloadOfRetryableTask(dto);
            policyService.createNewPolicy(policyDTO);
            return true;
        } catch (RuntimeException e) {
            log.error("Ошибка обработки события {}: {}", dto.getId(), e.getMessage());
            return false;
        }
    }
}
