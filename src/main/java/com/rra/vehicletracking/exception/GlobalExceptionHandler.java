package com.rra.vehicletracking.exception;

import com.rra.vehicletracking.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        logger.error("CustomException: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        logger.error("Validation errors: {}", errors);

        ApiResponse<Map<String, String>> response = new ApiResponse<>("error", "Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        logger.error("Constraint violations: {}", errors);

        ApiResponse<Map<String, String>> response = new ApiResponse<>("error", "Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.error("JSON parse error: {}", ex.getMessage());

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "Invalid JSON format. Please provide a properly formatted JSON object.");
        errorDetails.put("example", createExampleJson());

        ApiResponse<Map<String, Object>> response = new ApiResponse<>("error", 
            "Invalid JSON format. Please ensure your request body is a valid JSON object.", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> createExampleJson() {
        Map<String, Object> example = new HashMap<>();
        example.put("vehicleId", 1);
        example.put("fromOwnerId", 2);
        example.put("toOwnerId", 3);
        example.put("transferPrice", 5000.0);
        example.put("newPlateNumberId", 4);
        return example;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
            ApiResponse.error("An unexpected error occurred"), 
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
