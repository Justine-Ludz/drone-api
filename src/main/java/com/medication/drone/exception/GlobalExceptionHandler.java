package com.medication.drone.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@NoArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(Exception ex) {
        BadRequestException raisedException = (BadRequestException) ex;
        Map<String, Object> body = new HashMap<>();
        ApiErrorResponse errorResponse = new ApiErrorResponse(raisedException.getErrorId(), raisedException.getErrorMessage(), raisedException.getErrorDetails());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(Exception ex) {
        NotFoundException raisedException = (NotFoundException) ex;
        Map<String, Object> body = new HashMap<>();
        ApiErrorResponse errorResponse = new ApiErrorResponse(raisedException.getErrorId(), raisedException.getErrorMessage(), raisedException.getErrorDetails());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiErrorResponse> handleInternalServerException(Exception ex) {
        InternalServerException raisedException = (InternalServerException) ex;
        Map<String, Object> body = new HashMap<>();
        ApiErrorResponse errorResponse = new ApiErrorResponse(raisedException.getErrorId(), raisedException.getErrorMessage(), raisedException.getErrorDetails());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        Map<String, Object> responseDetails = new HashMap<>();

        responseDetails.put("timestamp", LocalDateTime.now());
        responseDetails.put("message", violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Validation Error", responseDetails);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(HttpMessageNotReadableException ex) {
        Map<String, Object> responseDetails = new HashMap<>();

        responseDetails.put("timestamp", LocalDateTime.now());
        responseDetails.put("message", ex.getMessage());

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Cannot be read Error", responseDetails);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Database Error");
        response.put("message", "A database constraint was violated. Please check your request.");
        response.put("path", request.getDescription(false));

        ApiErrorResponse errorResponse = new ApiErrorResponse(String.valueOf(HttpStatus.CONFLICT.value()), "Validation Error", response);

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        String.valueOf(HttpStatus.BAD_REQUEST.value()),
                        "Validation Error",
                        errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
