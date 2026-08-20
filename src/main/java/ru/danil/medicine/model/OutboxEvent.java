package ru.danil.medicine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "retry_time")
    private Instant retryTime;

    @Column(name = "lease_expires_at")
    private Instant leaseExpiresAt;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}