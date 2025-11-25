package com.jumbo.store.configuration.exception;

import com.jumbo.store.domain.util.SourceMessage;
import com.jumbo.store.domain.util.helper.MessageHelper;
import com.jumbo.store.domain.util.string.StringUtils;
import com.jumbo.store.web.dto.ErrorResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler {

    private final SourceMessage sourceMessage;

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        ErrorResponse error =
                getResponse(ex, sourceMessage.getMessage(MessageHelper.BUSINESS_ERROR), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public final ResponseEntity<Object> handlerMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public final ResponseEntity<Object> handlerMethodValidationException(HandlerMethodValidationException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public final ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        ErrorResponse error =
                getResponse(ex, sourceMessage.getMessage(MessageHelper.AUTHORIZATION_ERROR), HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public final ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
        ErrorResponse error =
                getResponse(ex, sourceMessage.getMessage(MessageHelper.RESOURCE_NOT_FOUND), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public final ResponseEntity<Object> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        ErrorResponse error = getResponse(
                ex, sourceMessage.getMessage(MessageHelper.METHOD_NOT_ALLOWED), HttpStatus.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public final ResponseEntity<Object> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse error = getResponse(
                ex, sourceMessage.getMessage(MessageHelper.METHOD_NOT_ALLOWED), HttpStatus.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<Object> handleArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorResponse error =
                getResponse(ex, sourceMessage.getMessage(MessageHelper.INVALID_ARGUMENT), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse error =
                getResponse(ex, sourceMessage.getMessage(MessageHelper.VALIDATION_ERROR), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse error =
                getResponse(ex, sourceMessage.getMessage(MessageHelper.INVALID_ARGUMENT), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error =
                getResponse(ex, sourceMessage.getMessage(MessageHelper.INVALID_LOCATION_ERROR), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CannotCreateTransactionException.class)
    public final ResponseEntity<Object> handleCannotCreateTransactionException(CannotCreateTransactionException ex) {
        // Check if the root cause is an IllegalArgumentException (validation error)
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof IllegalArgumentException) {
            ErrorResponse error = getResponse(
                    (IllegalArgumentException) rootCause,
                    sourceMessage.getMessage(MessageHelper.INVALID_LOCATION_ERROR),
                    HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        ErrorResponse error = getResponse(
                ex, sourceMessage.getMessage(MessageHelper.DATABASE_CONNECTION_ERROR), HttpStatus.SERVICE_UNAVAILABLE);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public final ResponseEntity<Object> handleCircuitBreakerOpenException(CallNotPermittedException ex) {
        ErrorResponse error = getResponse(
                ex, sourceMessage.getMessage(MessageHelper.CIRCUIT_BREAKER_ERROR), HttpStatus.SERVICE_UNAVAILABLE);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(DataAccessException.class)
    public final ResponseEntity<Object> handleDataAccessException(DataAccessException ex) {
        ErrorResponse error = getResponse(
                ex, sourceMessage.getMessage(MessageHelper.DATABASE_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ErrorResponse getResponse(Exception ex, String sourceMessage, HttpStatus httpStatus) {
        var details = List.of(StringUtils.cleanupErrorMessage(ex.getLocalizedMessage()));
        ErrorResponse error = ErrorResponse.builder()
                .message(sourceMessage)
                .httpStatus(httpStatus)
                .httpCode(httpStatus.value())
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
        log.error("Exception occurred: {}", error.getMessage(), ex);
        return error;
    }
}
