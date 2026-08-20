package ru.danil.medicine.exception;

public class PermanentDataException extends RuntimeException {

    public PermanentDataException(String message) {
        super(message);
    }

    public PermanentDataException(String message, Throwable cause) {
        super(message, cause);
    }
}