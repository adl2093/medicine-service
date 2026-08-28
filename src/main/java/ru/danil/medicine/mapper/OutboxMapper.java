package ru.danil.medicine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.model.OutboxEvent;
import ru.danil.medicine.utill.converter.PolicyEventPayloadConverter;

import java.time.Instant;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = PolicyEventPayloadConverter.class
)
public interface OutboxMapper {

    @Mapping(target = "retryTime", ignore = true)
    @Mapping(source = "task", target = "payload", qualifiedByName = "convertTaskToJson")
    OutboxEvent toOutboxEvent(RetryableTaskDTO task);

    default OutboxEvent toOutboxEventWithDefaults(RetryableTaskDTO task) {
        OutboxEvent event = toOutboxEvent(task);
        Instant now = Instant.now();
        event.setRetryTime(now);
        event.setLeaseExpiresAt(null);
        event.setCreatedAt(now);
        event.setUpdatedAt(now);
        event.setIsDeleted(false);
        event.setAttempts(0);
        return event;
    }

    @Mapping(source = "payload", target = ".", qualifiedByName = "convertPayloadToRetryableTaskDTO")
    RetryableTaskDTO toRetryableTaskDTOFromPayload(String payload);
}