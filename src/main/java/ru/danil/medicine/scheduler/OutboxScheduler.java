package ru.danil.medicine.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.danil.medicine.config.OutboxProperties;
import ru.danil.medicine.model.OutboxEvent;
import ru.danil.medicine.service.OutboxService;
import ru.danil.medicine.service.SendToDLQService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {
    private final OutboxService outboxService;
    private final SendToDLQService sendToDLQService;
    private final OutboxProperties outboxProperties;

    @Scheduled(fixedRateString = "${outbox-service.fixed-rate}")
    public void execute() {
        final List<OutboxEvent> events;
        try {
            events = outboxService.claimEvents();
        } catch (Exception e) {
            log.error("Ошибка при захвате outbox событий", e);
            return;
        }
        if (events.isEmpty()) {
            return;
        }
        log.info("Получено {} outbox событий для отправки в DLQ", events.size());
        for (OutboxEvent event : events) {
            processEvent(event);
        }
    }

    private void processEvent(OutboxEvent event) {
        try {
            sendToDLQService.send(event);
            outboxService.markAsDeleted(event.getId());
        } catch (Exception e) {
            log.error("Не удалось отправить outbox событие {} в DLQ", event.getId(), e);
            handleSendFailure(event,e);
        }
    }

    private void handleSendFailure(OutboxEvent event, Exception e) {
        try {
            if (event.getAttempts() + 1 >= outboxProperties.getMaxAttempts()) {
                outboxService.markAsDeleted(event.getId());
                log.error("Outbox событие {} достигло лимита попыток и помечено как удалённое", event.getId());
            } else {
                outboxService.reschedule(event.getId());
            }
        } catch (Exception rescheduleException) {
            log.error("Не удалось обновить состояние outbox события {}", event.getId(), rescheduleException);
        }
    }
}