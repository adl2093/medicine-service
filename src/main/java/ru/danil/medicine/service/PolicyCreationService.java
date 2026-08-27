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
    private final OutboxService outboxService;

    public void process(List<RetryableTaskDTO> tasks) {
        boolean retry = false;
        for (RetryableTaskDTO task : tasks) {
            try {
                policyTaskService.processSingleTask(task);
            } catch (Exception e) {
                TaskProcessingResult result = exceptionClassifier.classify(e);
                retry = handleResult(task, result, e, retry);
            }
        }
        if (retry) {
            throw new RetryableTaskException("Обработка батча требует retry");
        }
    }

    private boolean handleResult(RetryableTaskDTO task, TaskProcessingResult result, Exception e, boolean retry) {
        switch (result) {
            case RETRY:
                retry = true;
                log.warn("Задача {} требует повторной обработки (RETRY)", task.getId(), e);
                break;
            case PERMANENT_FAILURE:
                log.error("Задача {} содержит ошибку данных (PERMANENT), пропускаем", task.getId(), e);
                outboxService.saveDlqEvent(task);
                break;
            case UNEXPECTED:
                log.error("Неожиданная ошибка при обработке задачи {}, прерываем батч", task.getId(), e);
                throw new RetryableTaskException("Неожиданная ошибка при обработке задачи " + task.getId(), e);
            default:
                throw new IllegalStateException("Неизвестный результат классификации: " + result);
        }
        return retry;
    }
}