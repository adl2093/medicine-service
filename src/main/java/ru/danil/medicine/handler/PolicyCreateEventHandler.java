package ru.danil.medicine.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.service.PolicyCreationService;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyCreateEventHandler {
    private final PolicyCreationService processor;

    @KafkaListener(topics = "${kafka-listener.topic-name-in-policy-create-event-handler}")
    public void handle(List<RetryableTaskDTO> retryableTaskDTOs, Acknowledgment ack) {
        processor.process(retryableTaskDTOs);
        ack.acknowledge();
        log.info("Пачка обработана, комитим");
    }
}