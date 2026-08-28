package ru.danil.medicine.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.danil.medicine.dto.PolicyDTO;
import ru.danil.medicine.dto.RetryableTaskDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T00:14:58+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.4.1.jar, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class RetryableTaskMapperImpl implements RetryableTaskMapper {

    @Override
    public PolicyDTO toPolicyDTOFromPayloadOfRetryableTask(RetryableTaskDTO retryableTaskDTO) {
        if ( retryableTaskDTO == null ) {
            return null;
        }

        PolicyDTO policyDTO = new PolicyDTO();

        return policyDTO;
    }
}
