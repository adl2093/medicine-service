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
        UUID attemptId = UUID.randomUUID();
        return outboxRepository.claimEvents(now, leaseExpiresAt, properties.getLimit(), attemptId);
    }

    @Transactional
    public void markAsDeleted(OutboxEvent event) {
        Instant now = Instant.now();
        UUID eventId = event.getId();
        int updated = outboxRepository.markAsDeleted(event.getId(), now, event.getAttemptId());

        if (updated == 0) {
            log.warn("Outbox событие {} не найдено, уже обработано или lease истёк", eventId);
        } else {
            log.info("Outbox событие {} помечено как обработанное", eventId);
        }
    }

    @Transactional
    public void reschedule(OutboxEvent event) {
        Instant now = Instant.now();
        UUID eventId = event.getId();
        Instant nextRetry = now.plusSeconds(properties.getRetryDelaySeconds());
        int updated = outboxRepository.reschedule(eventId, event.getAttemptId(), nextRetry, now, properties.getMaxAttempts());

        if (updated == 0) {
            log.warn("Outbox событие {} не найдено, lease истёк или достигнут лимит попыток", eventId);
        } else {
            log.info("Outbox событие {} перепланировано", eventId);
        }
    }
}