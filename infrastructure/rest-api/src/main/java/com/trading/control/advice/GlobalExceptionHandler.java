package com.trading.control.advice;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.exception.ServiceUnavailableException;
import com.trading.control.application.domain.exception.ValidationException;
import com.trading.control.restapi.generated.model.ErrorResponseWebDto;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseWebDto handleNotFound(NotFoundException ex) {
        return error(HttpStatus.NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseWebDto handleDomainValidation(ValidationException ex) {
        return error(HttpStatus.BAD_REQUEST.name(), ex.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponseWebDto handleUnavailable(ServiceUnavailableException ex) {
        return error(HttpStatus.SERVICE_UNAVAILABLE.name(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseWebDto handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST.name(), message.isEmpty() ? "Validation failed" : message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseWebDto handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> lastNode(violation.getPropertyPath().toString()) + " " + violation.getMessage())
                .sorted()
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST.name(), message.isEmpty() ? "Validation failed" : message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseWebDto handleMissingParameter(MissingServletRequestParameterException ex) {
        return error(HttpStatus.BAD_REQUEST.name(), ex.getParameterName() + " is required");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseWebDto handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return error(HttpStatus.BAD_REQUEST.name(), ex.getName() + " has an invalid value");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseWebDto handleUnreadableBody(HttpMessageNotReadableException ex) {
        return error(HttpStatus.BAD_REQUEST.name(), "Malformed request body");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseWebDto> handleUnexpected(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            return handleFrameworkError(errorResponse);
        }
        return ResponseEntity.internalServerError()
                .body(error(HttpStatus.INTERNAL_SERVER_ERROR.name(), "An unexpected error occurred"));
    }

    private ResponseEntity<ErrorResponseWebDto> handleFrameworkError(ErrorResponse errorResponse) {
        HttpStatusCode status = errorResponse.getStatusCode();
        HttpStatus resolved = HttpStatus.resolve(status.value());
        String code = resolved != null ? resolved.name() : String.valueOf(status.value());
        return ResponseEntity.status(status)
                .headers(errorResponse.getHeaders())
                .body(error(code, errorResponse.getBody().getDetail()));
    }

    private ErrorResponseWebDto error(String code, String message) {
        var dto = new ErrorResponseWebDto();
        dto.setError(code);
        dto.setMessage(message);
        dto.setTimestamp(OffsetDateTime.now(clock));
        return dto;
    }

    private static String lastNode(String propertyPath) {
        return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
    }
}
