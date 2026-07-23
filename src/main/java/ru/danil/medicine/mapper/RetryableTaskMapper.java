package ru.danil.medicine.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import ru.danil.medicine.dto.PolicyDTO;
import ru.danil.medicine.dto.RetryableTaskDTO;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RetryableTaskMapper {

    @Mapping(source = "payload", target = ".", qualifiedByName = "convertPayloadToPolicyDTO")
    PolicyDTO toPolicyDTOFromPayloadOfRetryableTask(RetryableTaskDTO retryableTaskDTO);

    List<PolicyDTO> toPolicyDTOListFromPayloadOfRetryableTaskList(List<RetryableTaskDTO> retryableTaskDTOs);

    @Named("convertPayloadToPolicyDTO")
    default PolicyDTO convertPayloadToPolicyDTO(String json) {
        try {
            return new ObjectMapper().readValue(json, PolicyDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка парсинга PolicyDTO из JSON", e);
        }
    }
}
