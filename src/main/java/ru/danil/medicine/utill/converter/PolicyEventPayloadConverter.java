package ru.danil.medicine.utill.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.danil.medicine.dto.PolicyDTO;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.exception.PermanentDataException;

@Component
@RequiredArgsConstructor
public class PolicyEventPayloadConverter {
    private final ObjectMapper objectMapper;

    @Named("convertTaskToJson")
    public String toJson(RetryableTaskDTO task) {
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new PermanentDataException("Ошибка сериализации RetryableTaskDTO в outbox", e);
        }
    }

    @Named("convertPayloadToPolicyDTO")
    public PolicyDTO toPolicyDTO(String json) {
        try {
            return objectMapper.readValue(json, PolicyDTO.class);
        } catch (JsonProcessingException e) {
            throw new PermanentDataException("Некорректный payload события", e);
        }
    }

    @Named("convertPayloadToRetryableTaskDTO")
    public RetryableTaskDTO toRetryableTaskDTO(String json) {
        try {
            return objectMapper.readValue(json, RetryableTaskDTO.class);
        } catch (JsonProcessingException e) {
            throw new PermanentDataException("Некорректный payload события", e);
        }
    }
}