package ru.danil.medicine.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.danil.medicine.model.IdempotencyKey;

import java.util.List;
import java.util.UUID;

public interface IdempotencyKeyRepository extends CrudRepository<IdempotencyKey, UUID> {
    List<UUID> findByIdIn(List<UUID> ids);

    @Modifying
    @Query(value = """
    INSERT INTO idempotency_keys (id, created_at)
    SELECT unnest(:ids::uuid[]), now()
    """, nativeQuery = true)
    void insertBatch(List<UUID> ids);
}
