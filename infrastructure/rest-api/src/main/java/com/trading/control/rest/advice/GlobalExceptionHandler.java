package com.trading.control.rest.advice;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.restapi.generated.model.ErrorResponseWebDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseWebDto handleNotFound(NotFoundException ex) {
        var dto = new ErrorResponseWebDto();
        dto.setError("NOT_FOUND");
        dto.setMessage(ex.getMessage());
        dto.setTimestamp(OffsetDateTime.now());
        return dto;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseWebDto handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        var dto = new ErrorResponseWebDto();
        dto.setError("BAD_REQUEST");
        dto.setMessage(message.isEmpty() ? "Validation failed" : message);
        dto.setTimestamp(OffsetDateTime.now());
        return dto;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseWebDto handleUnexpected(Exception ex) {
        var dto = new ErrorResponseWebDto();
        dto.setError("INTERNAL_SERVER_ERROR");
        dto.setMessage("An unexpected error occurred");
        dto.setTimestamp(OffsetDateTime.now());
        return dto;
    }
}
