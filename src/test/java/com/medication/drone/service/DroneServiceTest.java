package com.medication.drone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medication.drone.dto.DroneDto;
import com.medication.drone.repository.entity.Drone;
import com.medication.drone.exception.InternalServerException;
import com.medication.drone.repository.DroneRepository;
import com.medication.drone.service.impl.DroneServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import java.math.BigInteger;

import static com.medication.drone.constants.DroneModelEnum.LIGHTWEIGHT;
import static com.medication.drone.constants.ErrorStatus.DATA_ACCESS_ERROR;
import static com.medication.drone.constants.ErrorStatus.INTERNAL_SERVER_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DroneServiceTest {

    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DroneServiceImpl droneService;

    @Test
    void DroneRegisterTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String response = """
                {
                    "id": 1,
                    "serialNumber": 1234567890,
                    "model": "LIGHTWEIGHT",
                    "weightLimit": 200.0,
                    "batteryCapacity": 99.9,
                    "state": "IDLE",
                    "medicine": null
                }
                """;
        Drone drone = mapper.readValue(response, Drone.class);

        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567890),
                LIGHTWEIGHT,
                99.9
        );

        when(droneRepository.save(any())).thenReturn(drone);
        Drone droneResponse = droneService.registerDrone(droneRequest);

        assertNotNull(droneResponse);
        assertEquals(drone.getState(), droneResponse.getState());
        assertEquals(drone.getWeightLimit(), droneResponse.getWeightLimit());
        assertEquals(drone.getSerialNumber(), droneResponse.getSerialNumber());
    }

    @Test
    void DataAccessError() {
        when(droneRepository.findBySerialNumber(any())).thenThrow(new DataRetrievalFailureException("Database connection failed"));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            droneService.retrieveDroneLoad(BigInteger.valueOf(1234567890));});

        assertNotNull(exception);
        assertEquals(DATA_ACCESS_ERROR.getErrorCode(), exception.getErrorId());
        assertEquals(DATA_ACCESS_ERROR.getErrorMessage(), exception.getErrorMessage());
    }

    @Test
    void RegisterDroneError() {
        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567890),
                LIGHTWEIGHT,
                99.9
        );

        when(droneRepository.save(any())).thenThrow(new DataRetrievalFailureException("Database connection failed"));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            droneService.registerDrone(droneRequest);});

        assertNotNull(exception);
        assertEquals(DATA_ACCESS_ERROR.getErrorCode(), exception.getErrorId());
        assertEquals(DATA_ACCESS_ERROR.getErrorMessage(), exception.getErrorMessage());
    }

    @Test
    void RegisterDroneInternalServerError() {
        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567890),
                LIGHTWEIGHT,
                99.9
        );

        when(droneRepository.save(any())).thenThrow(new InternalServerException("500","error has occured", null));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            droneService.registerDrone(droneRequest);});

        assertNotNull(exception);
        assertEquals(INTERNAL_SERVER_ERROR.getErrorCode(), exception.getErrorId());
        assertEquals(INTERNAL_SERVER_ERROR.getErrorMessage(), exception.getErrorMessage());
    }
}