package com.medication.drone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BatteryCapacityResponse {
    private BigInteger serialNumber;
    private String battery;
}
