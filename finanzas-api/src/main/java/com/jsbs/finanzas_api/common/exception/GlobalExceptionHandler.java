package com.jsbs.finanzas_api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return new ErrorResponse(
                400,
                "Validation failed",
                LocalDateTime.now(),
                errors
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCategoryNotFound(CategoryNotFoundException ex) {

        return new ErrorResponse(
                404,
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTransactionNotFound(TransactionNotFoundException ex) {
        return new ErrorResponse(
                404,
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmailExists(EmailAlreadyExistsException ex) {
        return new ErrorResponse(
                400,
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException ex) {
        return new ErrorResponse(
                401,
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

    @ExceptionHandler(CategoryInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerCategoryInUse(CategoryInUseException ex) {
        return new ErrorResponse(
                409,
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        return new ErrorResponse(
                500,
                "Unexpected internal server error",
                LocalDateTime.now(),
                null
        );
    }
}