package ru.danil.medicine.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.danil.medicine.model.IdempotencyKey;

import java.util.UUID;

@Repository
public interface IdempotencyKeyRepository extends CrudRepository<IdempotencyKey, UUID> {

    @Modifying
    @Query(value = """
            INSERT INTO idempotency_keys (id, created_at, updated_at, is_deleted)
            VALUES (:id, NOW(), NOW(), false)
            ON CONFLICT (id) DO NOTHING
            """, nativeQuery = true)
    int insert(UUID id);
}