package com.medication.drone.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class InternalServerException extends RuntimeException {

    private final String errorId;
    private final String errorMessage;
    private final Map<String, Object> errorDetails;
    public InternalServerException(String errorId, String errorMessage, Map<String, Object> errorDetails) {
        this.errorId = errorId;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
    }
}
