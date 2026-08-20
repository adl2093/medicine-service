package ru.danil.medicine.mapper;

import org.mapstruct.*;
import ru.danil.medicine.dto.PolicyDTO;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.utill.converter.PolicyEventPayloadConverter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = PolicyEventPayloadConverter.class)
public interface RetryableTaskMapper {

    @Mapping(source = "payload", target = ".", qualifiedByName = "convertPayloadToPolicyDTO")
    PolicyDTO toPolicyDTOFromPayloadOfRetryableTask(RetryableTaskDTO retryableTaskDTO);
}