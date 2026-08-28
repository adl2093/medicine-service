package ru.danil.medicine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danil.medicine.repository.IdempotencyKeyRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyKeyService {
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional
    public int insertIdempotencyKey(UUID id) {
        return idempotencyKeyRepository.insert(id);
    }
}