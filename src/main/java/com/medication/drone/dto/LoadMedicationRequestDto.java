package com.medication.drone.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoadMedicationRequestDto {
    BigInteger droneSerialNumber;

    @Valid
    MedicationDto medication;
}
