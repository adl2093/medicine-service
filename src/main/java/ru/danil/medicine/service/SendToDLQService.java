package ru.danil.medicine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.danil.medicine.config.KafkaTopicProperties;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.mapper.OutboxMapper;
import ru.danil.medicine.model.OutboxEvent;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendToDLQService {
    private final KafkaTemplate<String, RetryableTaskDTO> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;
    private final OutboxMapper outboxMapper;

    public void send(OutboxEvent event) throws Exception {
        String topic = kafkaTopicProperties.getPolicyCreatedDlqName();
        try {
            RetryableTaskDTO dto = outboxMapper.toRetryableTaskDTOFromPayload(event.getPayload());
            kafkaTemplate.send(topic, event.getId().toString(), dto).get();
            log.info("Outbox событие {} успешно отправлено в DLQ, attempt={}", event.getId(), event.getAttempts() + 1
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Поток прерван при отправке события " + event.getId(), e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Kafka не подтвердила отправку события " + event.getId(), e.getCause());
        }
    }
}