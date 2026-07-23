package ru.danil.medicine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "idempotency_keys")
public class IdempotencyKey {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}