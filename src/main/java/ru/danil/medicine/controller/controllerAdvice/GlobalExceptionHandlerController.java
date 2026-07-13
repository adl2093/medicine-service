package ru.danil.medicine.controller.controllerAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.danil.medicine.dto.ErrorResponse;
import ru.danil.medicine.exception.ObjectNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserException(ObjectNotFoundException e) {
        return new ErrorResponse().message(e.getMessage());
    }
}
