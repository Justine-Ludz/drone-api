package com.medication.drone.controller;

import com.medication.drone.dto.*;
import com.medication.drone.repository.entity.Drone;
import com.medication.drone.service.DroneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.StringConcatFactory;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/drone")
public class DroneTowerController {

    @Autowired
    private DroneService droneService;

    @PostMapping("/register")
    public ResponseEntity<Drone> registerDrone (@Valid @RequestBody DroneDto drone) {
        return ResponseEntity.ok(droneService.registerDrone(drone)); //add drone slots left
    }

    @PostMapping("/load-medication")
    public ResponseEntity<Drone> loadMedication (@Valid @RequestBody LoadMedicationRequestDto loadRequest) {
        return ResponseEntity.ok(droneService.loadMedicine(loadRequest)); //add drone slots left
    }

    @GetMapping("/drone-load/{serial}")
    public ResponseEntity<List<MedicationDto>> getDroneLoad (@PathVariable BigInteger serial) {
        return ResponseEntity.ok(droneService.retrieveDroneLoad(serial)); //add drone slots left
    }

    @GetMapping("/drone-availability/{serial}")
    public ResponseEntity<DroneLoadAvailabilityDto> getDroneAvailability (@PathVariable BigInteger serial) {
        return ResponseEntity.ok(droneService.droneLoadAvailability(serial)); //add drone slots left
    }

    @GetMapping("/drone-Battery/{serial}")
    public ResponseEntity<BatteryCapacityResponse> retrieveDroneBattery(@PathVariable BigInteger serial) {
        Double batteryPercentage = droneService.retrieveDroneBattery(serial);
        BatteryCapacityResponse response = BatteryCapacityResponse.builder()
                .serialNumber(serial)
                .battery(String.format("%.2f %%", batteryPercentage))
                .build();
        return ResponseEntity.ok(response); //add drone slots left
    }
}
