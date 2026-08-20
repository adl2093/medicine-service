package ru.danil.medicine.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.danil.medicine.model.OutboxEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends CrudRepository<OutboxEvent, UUID> {

    @Query(value = """
            WITH candidates AS (
                SELECT id
                FROM outbox_events
                WHERE is_deleted = false
                  AND retry_time <= :now
                  AND (
                      lease_expires_at IS NULL
                      OR lease_expires_at < :now
                  )
                ORDER BY retry_time ASC
                LIMIT :limit
                FOR UPDATE SKIP LOCKED
            )
            UPDATE outbox_events o
            SET lease_expires_at = :leaseExpiresAt, updated_at = :now
            FROM candidates c
            WHERE o.id = c.id
            RETURNING o.*
            """,
            nativeQuery = true)
    List<OutboxEvent> claimEvents(Instant now, Instant leaseExpiresAt, int limit);

    @Modifying
    @Query("""
            UPDATE OutboxEvent o
            SET o.isDeleted = true,
                o.updatedAt = :now,
                o.leaseExpiresAt = null
            WHERE o.id = :id
              AND o.isDeleted = false
              AND o.leaseExpiresAt >= :now
            """)
    int markAsDeleted(UUID id, Instant now);

    @Modifying
    @Query("""
            UPDATE OutboxEvent o
            SET o.retryTime = :retryTime,
                o.attempts = o.attempts + 1,
                o.updatedAt = :now,
                o.leaseExpiresAt = null
            WHERE o.id = :id
              AND o.isDeleted = false
              AND o.leaseExpiresAt >= :now
              AND o.attempts < :maxAttempts
            """)
    int reschedule(UUID id, Instant retryTime, Instant now, int maxAttempts);
}