package ru.danil.medicine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danil.medicine.dto.PolicyDTO;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.mapper.RetryableTaskMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyTaskService {

    private final IdempotencyKeyService idempotencyKeyService;
    private final RetryableTaskMapper retryableTaskMapper;
    private final PolicyService policyService;
    private final OutboxService outboxService;

    @Transactional
    public void processSingleTask(RetryableTaskDTO dto) {
        int inserted = idempotencyKeyService.insertIdempotencyKey(dto.getId());
        if (inserted == 0) {
            log.info("Событие {} уже обработано", dto.getId());
            return;
        }
        createPolicy(dto);
    }

    private void createPolicy(RetryableTaskDTO dto) {
            PolicyDTO policyDTO = retryableTaskMapper.toPolicyDTOFromPayloadOfRetryableTask(dto);
            policyService.createNewPolicy(policyDTO);
    }
}