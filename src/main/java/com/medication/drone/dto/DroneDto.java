package com.medication.drone.dto;

import com.medication.drone.constants.DroneModelEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DroneDto {
    @NotNull(message = "Serial Number cannot be null")
    @Digits(integer = 100, fraction = 0, message = "Serial number must be at most 100 digits")
    @Min(value = 9999L, message = "Serial number must be at least 5 digits")
    private BigInteger serialNumber;

    @NotNull(message = "Model cannot be null")
    private DroneModelEnum model;

    @Max(100)
    @NotNull(message = "Battery capacity cannot be null")
    private Double batteryCapacity;
}
