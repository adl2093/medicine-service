package ru.danil.medicine.exception;

public class RetryableTaskException extends RuntimeException {

    public RetryableTaskException(String message) {
        super(message);
    }

    public RetryableTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}