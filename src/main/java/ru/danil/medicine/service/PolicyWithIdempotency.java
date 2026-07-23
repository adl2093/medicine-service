package ru.danil.medicine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danil.medicine.dto.PolicyDTO;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.mapper.RetryableTaskMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PolicyWithIdempotency {
    private final PolicyService policyService;
    private final IdempotencyKeyService idempotencyKeyService;
    private final RetryableTaskMapper retryableTaskMapper;

    @Transactional
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
