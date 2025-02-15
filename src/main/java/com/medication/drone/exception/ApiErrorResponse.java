package com.medication.drone.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiErrorResponse {
    private String errorCode;
    private String errorMessage;
    private Map<String,Object> errorDetails;
}
