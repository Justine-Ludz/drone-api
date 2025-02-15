package com.medication.drone.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum ErrorStatus {
    DRONE_NOT_FOUND(String.valueOf(HttpStatus.NOT_FOUND.value()), "Drone not found", timeStamper()),
    DATA_ACCESS_ERROR(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "Error while interacting with the database", timeStamper()),
    INTERNAL_SERVER_ERROR(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "Error encountered", timeStamper()),
    BAD_REQUEST_ERROR(String.valueOf(HttpStatus.BAD_REQUEST.value()), null, timeStamper());

    private final String errorCode;
    private final String errorMessage;
    private final Map<String,Object> details;
    ErrorStatus(String errorCode, String errorMessage, Map<String, Object> details) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.details = details;
    }

    public static Map<String,Object> timeStamper(){
        Map<String,Object> time = new HashMap<>();
        time.put("time", LocalDateTime.now());
        return time;
    }
}


