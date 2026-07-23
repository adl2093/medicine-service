package ru.danil.medicine.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.danil.medicine.dto.PolicyDTO;
import ru.danil.medicine.dto.RetryableTaskDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-23T12:16:58+0300",
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

    @Override
    public List<PolicyDTO> toPolicyDTOListFromPayloadOfRetryableTaskList(List<RetryableTaskDTO> retryableTaskDTOs) {
        if ( retryableTaskDTOs == null ) {
            return null;
        }

        List<PolicyDTO> list = new ArrayList<PolicyDTO>( retryableTaskDTOs.size() );
        for ( RetryableTaskDTO retryableTaskDTO : retryableTaskDTOs ) {
            list.add( toPolicyDTOFromPayloadOfRetryableTask( retryableTaskDTO ) );
        }

        return list;
    }
}
