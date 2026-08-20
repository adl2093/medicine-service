package ru.danil.medicine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danil.medicine.config.OutboxProperties;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.mapper.OutboxMapper;
import ru.danil.medicine.model.OutboxEvent;
import ru.danil.medicine.repository.OutboxRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final OutboxMapper outboxMapper;
    private final OutboxProperties properties;

    @Transactional
    public void saveDlqEvent(RetryableTaskDTO task) {
        OutboxEvent event = outboxMapper.toOutboxEventWithDefaults(task);
        outboxRepository.save(event);
        log.info("Outbox событие для задачи {} сохранено", task.getId());
    }

    @Transactional
    public List<OutboxEvent> claimEvents() {
        Instant now = Instant.now();
        Instant leaseExpiresAt = now.plusSeconds(properties.getProcessingLeaseSeconds());
        return outboxRepository.claimEvents(now, leaseExpiresAt, properties.getLimit());
    }

    @Transactional
    public void markAsDeleted(UUID id) {
        Instant now = Instant.now();
        int updated = outboxRepository.markAsDeleted(id, now);

        if (updated == 0) {
            log.warn("Outbox событие {} не найдено, уже обработано или lease истёк", id);
        } else {
            log.info("Outbox событие {} помечено как обработанное", id);
        }
    }

    @Transactional
    public void reschedule(UUID id) {
        Instant now = Instant.now();
        Instant nextRetry = now.plusSeconds(properties.getRetryDelaySeconds());
        int updated = outboxRepository.reschedule(id, nextRetry, now, properties.getMaxAttempts());

        if (updated == 0) {
            log.warn("Outbox событие {} не найдено, lease истёк или достигнут лимит попыток", id);
        } else {
            log.info("Outbox событие {} перепланировано", id);
        }
    }
}