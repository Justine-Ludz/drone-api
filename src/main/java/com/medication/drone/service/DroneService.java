package com.medication.drone.service;

import com.medication.drone.dto.*;
import com.medication.drone.repository.entity.Drone;

import java.math.BigInteger;
import java.util.List;

public interface DroneService {
    Drone registerDrone(DroneDto drone);
    Double retrieveDroneBattery(BigInteger serialNumber);
    Drone loadMedicine(LoadMedicationRequestDto loadRequest);
    DroneLoadAvailabilityDto droneLoadAvailability(BigInteger serialNumber);
    List<MedicationDto> retrieveDroneLoad(BigInteger serialNumber);
}
