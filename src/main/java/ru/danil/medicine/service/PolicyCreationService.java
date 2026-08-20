package ru.danil.medicine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.danil.medicine.dto.RetryableTaskDTO;
import ru.danil.medicine.exception.RetryableTaskException;
import ru.danil.medicine.exception.classifier.TaskExceptionClassifier;
import ru.danil.medicine.model.enums.TaskProcessingResult;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PolicyCreationService {
    private final PolicyTaskService policyTaskService;
    private final TaskExceptionClassifier exceptionClassifier;

    public void process(List<RetryableTaskDTO> tasks) {
        boolean retry = false;
        for (RetryableTaskDTO task : tasks) {
            try {
                policyTaskService.processSingleTask(task);
            } catch (Exception e) {
                TaskProcessingResult result = exceptionClassifier.classify(e);
                if (result == TaskProcessingResult.RETRY) {
                    retry = true;
                    log.warn("Событие {} будет обработано повторно", task.getId(), e);
                } else {
                    throw e;
                }
            }
        }
        if (retry) {
            throw new RetryableTaskException("Обработка батча требует retry");
        }
    }
}