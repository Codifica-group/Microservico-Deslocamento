package com.taxa.deslocamento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CoordenadasNotFoundException.class)
    public ResponseEntity<ApiError> handleCoordenadasNotFoundException(CoordenadasNotFoundException ex) {
        ApiError errorResponse = new ApiError(HttpStatus.NOT_FOUND.name(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIIntegrationException.class)
    public ResponseEntity<ApiError> handleAPIIntegrationException(APIIntegrationException ex) {
        ApiError errorResponse = new ApiError(HttpStatus.SERVICE_UNAVAILABLE.name(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
