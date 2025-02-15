package com.medication.drone.service.impl;

import com.medication.drone.constants.DroneModelWeightSpec;
import com.medication.drone.constants.DroneState;
import com.medication.drone.dto.*;
import com.medication.drone.repository.entity.Drone;
import com.medication.drone.repository.entity.Medicine;
import com.medication.drone.exception.BadRequestException;
import com.medication.drone.exception.InternalServerException;
import com.medication.drone.exception.NotFoundException;
import com.medication.drone.repository.DroneRepository;
import com.medication.drone.service.DroneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.medication.drone.constants.ErrorStatus.BAD_REQUEST_ERROR;
import static com.medication.drone.constants.ErrorStatus.DATA_ACCESS_ERROR;
import static com.medication.drone.constants.ErrorStatus.DRONE_NOT_FOUND;
import static com.medication.drone.constants.ErrorStatus.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
public class DroneServiceImpl implements DroneService {

    @Autowired
    private DroneRepository droneRepository;

    @Override
    public Drone registerDrone(DroneDto drone) {
        try{
            List<Drone> drones = (List<Drone>) droneRepository.findAll();
            long droneCount = drones.size();
            log.info(String.valueOf(droneCount));
            if(droneCount <= 9){
                final double weightLimit = DroneModelWeightSpec.valueOf(drone.getModel().toString()).getWeight();
                Drone droneOne = Drone.builder()
                        .serialNumber(drone.getSerialNumber())
                        .model(drone.getModel())
                        .weightLimit(weightLimit)
                        .batteryCapacity(drone.getBatteryCapacity())
                        .state(DroneState.IDLE)
                        .build();

                return droneRepository.save(droneOne);
            } else {
                log.error("Drone count is already at max!");
                throw new BadRequestException(
                        BAD_REQUEST_ERROR.getErrorCode(),
                        "Only 10 drones are allowed",
                        BAD_REQUEST_ERROR.getDetails()
                );
            }
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw new InternalServerException(
                    DATA_ACCESS_ERROR.getErrorCode(),
                    DATA_ACCESS_ERROR.getErrorMessage(),
                    DATA_ACCESS_ERROR.getDetails()
            );
        } catch (InternalServerException ex) {
            log.error(ex.getMessage());
            throw new InternalServerException(
                    INTERNAL_SERVER_ERROR.getErrorCode(),
                    INTERNAL_SERVER_ERROR.getErrorMessage(),
                    INTERNAL_SERVER_ERROR.getDetails()
            );
        }

    }

    @Override
    public Double retrieveDroneBattery(BigInteger serialNumber) {
        Drone drone = droneSearch(serialNumber);
        return drone.getBatteryCapacity();
    }

    @Override
    public Drone loadMedicine(LoadMedicationRequestDto loadRequest) {
        MedicationDto meds = loadRequest.getMedication();

        Drone drone = droneSearch(loadRequest.getDroneSerialNumber());
        log.info(String.valueOf(drone.getState().name().equals(DroneState.IDLE.name())));
        if(drone.getState().name().equals(DroneState.IDLE.name()) && isOverCapacity(drone, meds.getWeight())){
            drone.getMedicine().add(
                    Medicine.builder()
                            .name(meds.getName())
                            .weight(meds.getWeight())
                            .code(meds.getCode())
                            .imageURL(meds.getImageURL())
                            .isActive(true)
                            .drone(drone)
                            .build()
            );
            return droneRepository.save(drone);
        } else if (!isOverCapacity(drone, meds.getWeight())){
            log.error("Overcapacity!");
            throw new BadRequestException(
                    BAD_REQUEST_ERROR.getErrorCode(),
                    "Overcapacity! adding this medicine will overload the drone",
                    BAD_REQUEST_ERROR.getDetails()
            );
        } else if (!drone.getState().name().equals(DroneState.IDLE.name())) {
            log.error("Drone unavailable!");
            throw new BadRequestException(
                    BAD_REQUEST_ERROR.getErrorCode(),
                    "Drone unavailable! drone is busy at the moment",
                    BAD_REQUEST_ERROR.getDetails()
            );
        }
        return null;
    }

    @Override
    public DroneLoadAvailabilityDto droneLoadAvailability(BigInteger serialNumber) {
        Drone drone = droneSearch(serialNumber);
        boolean isAvailable = false;
        double weightTotal = 0.0;
        double remainingLoad = 0.0;

        for(Medicine meds : drone.getMedicine()){
            weightTotal += meds.getWeight();
        }

        if(weightTotal < drone.getWeightLimit()){
            isAvailable = true;
            remainingLoad =  drone.getWeightLimit() - weightTotal;
            return DroneLoadAvailabilityDto.builder()
                    .droneSerialNumber(drone.getSerialNumber())
                    .isAvailable(isAvailable)
                    .availableLoad(remainingLoad)
                    .build();
        } else {
            return DroneLoadAvailabilityDto.builder()
                    .droneSerialNumber(drone.getSerialNumber())
                    .isAvailable(false)
                    .availableLoad(0.0)
                    .build();
        }
    }

    @Override
    public List<MedicationDto> retrieveDroneLoad(BigInteger serialNumber) {
        Drone drone = droneSearch(serialNumber);
        return medicineToMedicationDto(drone.getMedicine());
    }

    private List<MedicationDto> medicineToMedicationDto(List<Medicine> medicineList) {
        List<MedicationDto> medicationDtoList = new ArrayList<>();

        for(Medicine med : medicineList) {
            medicationDtoList.add(MedicationDto.builder()
                    .name(med.getName())
                    .code(med.getCode())
                    .weight(med.getWeight())
                    .imageURL(med.getImageURL())
                    .build()
            );
        }
        return medicationDtoList;
    }

    private boolean isOverCapacity(Drone drone, double incomingWeight) {
        double totalLoad = 0;
        for(Medicine meds : drone.getMedicine()) {
            totalLoad += meds.getWeight();
        }
        double simulatedTotal = totalLoad + incomingWeight;
        return  simulatedTotal <= drone.getWeightLimit();
    }

    private Drone droneSearch(BigInteger serialNumber) {
        try{
            log.info("fetching drone with serial number: {}", serialNumber);
            Optional<Drone> droneOptional = droneRepository.findBySerialNumber(serialNumber);
            if(droneOptional.isPresent()){
                return droneOptional.get();
            } else {
                log.error("Drone not found");
                throw new NotFoundException(
                        DRONE_NOT_FOUND.getErrorCode(),
                        DRONE_NOT_FOUND.getErrorMessage(),
                        DRONE_NOT_FOUND.getDetails()
                );
            }
        } catch (DataAccessException e) {
            log.error(e.toString());
            throw new InternalServerException(
                    DATA_ACCESS_ERROR.getErrorCode(),
                    DATA_ACCESS_ERROR.getErrorMessage(),
                    DATA_ACCESS_ERROR.getDetails()
            );
        }
    }
}
