package com.medication.drone.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class NotFoundException extends RuntimeException {
    private final String errorId;
    private final String errorMessage;
    private final Map<String, Object> errorDetails;
    public NotFoundException(String errorId, String errorMessage, Map<String, Object> errorDetails) {
        this.errorId = errorId;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
    }
}
