package ru.danil.medicine.mapper;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.model.OutboxEvent;
import ru.danil.medicine.utill.converter.PolicyEventPayloadConverter;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T00:14:58+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.4.1.jar, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class OutboxMapperImpl implements OutboxMapper {

    @Autowired
    private PolicyEventPayloadConverter policyEventPayloadConverter;

    @Override
    public OutboxEvent toOutboxEvent(RetryableTaskDTO task) {
        if ( task == null ) {
            return null;
        }

        OutboxEvent outboxEvent = new OutboxEvent();

        outboxEvent.setPayload( policyEventPayloadConverter.toJson( task ) );
        outboxEvent.setId( task.getId() );

        return outboxEvent;
    }

    @Override
    public RetryableTaskDTO toRetryableTaskDTOFromPayload(String payload) {
        if ( payload == null ) {
            return null;
        }

        RetryableTaskDTO retryableTaskDTO = new RetryableTaskDTO();

        retryableTaskDTO.setPayload( payload );

        return retryableTaskDTO;
    }
}
