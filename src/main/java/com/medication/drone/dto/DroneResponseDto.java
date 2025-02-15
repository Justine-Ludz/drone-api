package com.medication.drone.dto;

import com.medication.drone.constants.DroneModelEnum;
import com.medication.drone.constants.DroneState;
import com.medication.drone.repository.entity.Medicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DroneResponseDto {

    private Long id;

    private BigInteger serialNumber;

    private DroneModelEnum model;

    private Double weightLimit;

    private String batteryCapacity;

    private DroneState state;

    private List<Medicine> medicine;
}
