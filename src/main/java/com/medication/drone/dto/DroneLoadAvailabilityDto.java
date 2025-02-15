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
public class DroneLoadAvailabilityDto {

    private boolean isAvailable;
    private BigInteger droneSerialNumber;

    //remaining load = weight capacity - total load weight
    private Double availableLoad;
}
