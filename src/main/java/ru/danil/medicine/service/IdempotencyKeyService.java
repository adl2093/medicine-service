package ru.danil.medicine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danil.medicine.repository.IdempotencyKeyRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyKeyService {
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional(readOnly = true)
    public List<UUID> findByIdIn(List<UUID> ids) {
        return idempotencyKeyRepository.findByIdIn(ids);
    }

    @Transactional
    public void insertBatch(List<UUID> ids) {
        idempotencyKeyRepository.insertBatch(ids);
    }
}
