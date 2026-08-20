package ru.danil.medicine.exception.classifier;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import ru.danil.medicine.exception.PermanentDataException;
import ru.danil.medicine.model.enums.TaskProcessingResult;

@Component
public class TaskExceptionClassifier {

    public TaskProcessingResult classify(Exception exception) {

        if (isPermanentDataException(exception)) {
            return TaskProcessingResult.PERMANENT_FAILURE;
        }

        if (isRetryableInfrastructureException(exception)) {
            return TaskProcessingResult.RETRY;
        }

        return TaskProcessingResult.RETRY;
    }

    private boolean isPermanentDataException(Throwable exception) {
        Throwable current = exception;

        while (current != null) {
            if (current instanceof PermanentDataException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isRetryableInfrastructureException(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof CannotCreateTransactionException
                    || current instanceof DataAccessResourceFailureException
                    || current instanceof QueryTimeoutException
                    || current instanceof CannotAcquireLockException
                    || current instanceof PessimisticLockingFailureException
                    || current instanceof DeadlockLoserDataAccessException
                    || current instanceof TransientDataAccessException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}