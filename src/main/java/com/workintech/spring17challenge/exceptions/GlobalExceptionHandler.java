package com.workintech.spring17challenge.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Özel olarak fırlatılan hataları yakalar
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException e) {
        ApiErrorResponse response = new ApiErrorResponse(e.getHttpStatus().value(), e.getMessage(), System.currentTimeMillis());
        log.error("Exception occured= ", e);
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception e) {
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bilinmeyen bir hata oluştu.", System.currentTimeMillis());
        log.error("Exception occured= ", e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}