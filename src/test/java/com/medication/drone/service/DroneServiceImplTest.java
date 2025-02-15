package com.medication.drone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medication.drone.dto.DroneDto;
import com.medication.drone.dto.DroneLoadAvailabilityDto;
import com.medication.drone.dto.LoadMedicationRequestDto;
import com.medication.drone.dto.MedicationDto;
import com.medication.drone.repository.entity.Drone;
import com.medication.drone.exception.BadRequestException;
import com.medication.drone.exception.NotFoundException;
import com.medication.drone.service.impl.DroneServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigInteger;
import java.util.List;

import static com.medication.drone.constants.DroneModelEnum.LIGHTWEIGHT;
import static com.medication.drone.constants.ErrorStatus.BAD_REQUEST_ERROR;
import static com.medication.drone.constants.ErrorStatus.DRONE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;


@SpringBootTest
@Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DroneServiceImplTest {

    @Autowired
    private DroneServiceImpl droneService;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void DroneRegisterTest() throws JsonProcessingException {
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

        Drone droneResponse = droneService.registerDrone(droneRequest);
        assertNotNull(droneResponse);
        assertEquals(drone.getId(), droneResponse.getId());
        assertEquals(drone.getState(), droneResponse.getState());
        assertEquals(drone.getWeightLimit(), droneResponse.getWeightLimit());
        assertEquals(drone.getSerialNumber(), droneResponse.getSerialNumber());
        assertEquals(drone.getBatteryCapacity(), droneResponse.getBatteryCapacity());
        assertEquals(drone.getModel(), droneResponse.getModel());
        assertNull(drone.getMedicine());
        assertNull(droneResponse.getMedicine());
    }

    @Test
    void DroneRegisterErrorTest() throws JsonProcessingException {
        fillDroneDB();
        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567100),
                LIGHTWEIGHT,
                99.9
        );

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            droneService.registerDrone(droneRequest);});

        assertNotNull(exception);
        assertEquals(BAD_REQUEST_ERROR.getErrorCode(), exception.getErrorId());
        assertEquals("Only 10 drones are allowed", exception.getErrorMessage());

    }

    @Test
    void MedicationLoad() throws JsonProcessingException {
        String response = """
                {
                    "id": 1,
                    "serialNumber": 1234567890,
                    "model": "LIGHTWEIGHT",
                    "weightLimit": 200.0,
                    "batteryCapacity": 99.9,
                    "state": "IDLE",
                    "medicine": [
                        {
                            "id": 1,
                            "name": "Pantoloc",
                            "weight": 195.0,
                            "code": "PKTL",
                            "imageURL": "https://nhathuocviet.vn/data/items/3817/pantoloc-20mg-1.jpg",
                            "active": true
                        }
                    ]
                }
                """;
        Drone drone = mapper.readValue(response, Drone.class);

        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567890),
                LIGHTWEIGHT,
                99.9
        );

        LoadMedicationRequestDto loadRequest = new LoadMedicationRequestDto();
        loadRequest.setDroneSerialNumber(BigInteger.valueOf(1234567890));
        loadRequest.setMedication(
                MedicationDto.builder()
                        .name("Paktal7")
                        .weight(195.0)
                        .code("PKTL")
                        .imageURL("https://nhathuocviet.vn/data/items/3817/pantoloc-20mg-1.jpg")
                        .build()
        );

        droneService.registerDrone(droneRequest);
        Drone droneResponse = droneService.loadMedicine(loadRequest);

        assertNotNull(droneResponse);
        assertEquals(drone.getState(), droneResponse.getState());
        assertEquals(drone.getWeightLimit(), droneResponse.getWeightLimit());
        assertEquals(drone.getSerialNumber(), droneResponse.getSerialNumber());
        assertEquals(drone.getBatteryCapacity(), droneResponse.getBatteryCapacity());
        assertEquals(drone.getModel(), droneResponse.getModel());

        assertEquals(drone.getMedicine().get(0).getCode(), droneResponse.getMedicine().get(0).getCode());

    }

    @Test
    void MedicationLoadErrorTest() throws JsonProcessingException {
        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567890),
                LIGHTWEIGHT,
                99.9
        );

        LoadMedicationRequestDto loadRequest = new LoadMedicationRequestDto();
        loadRequest.setDroneSerialNumber(BigInteger.valueOf(1234567890));
        loadRequest.setMedication(
                MedicationDto.builder()
                        .name("Paktal7")
                        .weight(333.0)
                        .code("PKTL")
                        .imageURL("https://nhathuocviet.vn/data/items/3817/pantoloc-20mg-1.jpg")
                        .build()
        );

        droneService.registerDrone(droneRequest);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            droneService.loadMedicine(loadRequest);});

        assertNotNull(exception);
        assertEquals(BAD_REQUEST_ERROR.getErrorCode(), exception.getErrorId());
        assertEquals("Overcapacity! adding this medicine will overload the drone", exception.getErrorMessage());
    }

    @Test
    void SearchErrorTest() throws JsonProcessingException {
        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567890),
                LIGHTWEIGHT,
                99.9
        );

        LoadMedicationRequestDto loadRequest = new LoadMedicationRequestDto();
        loadRequest.setDroneSerialNumber(BigInteger.valueOf(1234567100));
        loadRequest.setMedication(
                MedicationDto.builder()
                        .name("Paktal7")
                        .weight(333.0)
                        .code("PKTL")
                        .imageURL("https://nhathuocviet.vn/data/items/3817/pantoloc-20mg-1.jpg")
                        .build()
        );

        droneService.registerDrone(droneRequest);


        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            droneService.loadMedicine(loadRequest);});

        assertNotNull(exception);
        assertEquals(DRONE_NOT_FOUND.getErrorCode(), exception.getErrorId());
        assertEquals(DRONE_NOT_FOUND.getErrorMessage(), exception.getErrorMessage());
    }

    @Test
    void getBatteryTest(){
        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567890),
                LIGHTWEIGHT,
                99.9
        );
        droneService.registerDrone(droneRequest);

        Double expectedResponse = 99.9;
        BigInteger requestDroneSerial = BigInteger.valueOf(1234567890);

        Double serviceResponse = droneService.retrieveDroneBattery(requestDroneSerial);

        assertNotNull(serviceResponse);
        assertEquals(expectedResponse, serviceResponse);;
    }

    @Test
    void droneAvailabilityTest() throws JsonProcessingException {
        String response = """
                {
                     "droneSerialNumber": 1234567890,
                     "availableLoad": 5.0,
                     "available": true
                }
                """;

        DroneLoadAvailabilityDto expectedResponse = mapper.readValue(response, DroneLoadAvailabilityDto.class);

        loadInSampleDrone();

        BigInteger requestDroneSerial = BigInteger.valueOf(1234567890);
        DroneLoadAvailabilityDto actualResponse = droneService.droneLoadAvailability(requestDroneSerial);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getAvailableLoad(), actualResponse.getAvailableLoad());
        assertEquals(expectedResponse.getDroneSerialNumber(), actualResponse.getDroneSerialNumber());
        assertEquals(expectedResponse.isAvailable(), actualResponse.isAvailable());
    }

    @Test
    void droneAvailabilityTestNoAvailable() throws JsonProcessingException {
        String response = """
                {
                     "droneSerialNumber": 1234567890,
                     "availableLoad": 0.0,
                     "available": false
                }
                """;

        DroneLoadAvailabilityDto expectedResponse = mapper.readValue(response, DroneLoadAvailabilityDto.class);

        loadInSampleDrone();

        LoadMedicationRequestDto loadRequest = new LoadMedicationRequestDto();
        loadRequest.setDroneSerialNumber(BigInteger.valueOf(1234567890));
        loadRequest.setMedication(
                MedicationDto.builder()
                        .name("Paktal8")
                        .weight(5.0)
                        .code("PKTLE")
                        .imageURL("https://nhathuocviet.vn/data/items/3817/pantoloc-20mg-1.jpg")
                        .build()
        );

        droneService.loadMedicine(loadRequest);

        BigInteger requestDroneSerial = BigInteger.valueOf(1234567890);
        DroneLoadAvailabilityDto actualResponse = droneService.droneLoadAvailability(requestDroneSerial);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getAvailableLoad(), actualResponse.getAvailableLoad());
        assertEquals(expectedResponse.getDroneSerialNumber(), actualResponse.getDroneSerialNumber());
        assertEquals(expectedResponse.isAvailable(), actualResponse.isAvailable());
    }

    @Test
    void medLoadTest() throws JsonProcessingException {
        String response = """
                    {
                        "name": "Paktal7",
                        "weight": 195.0,
                        "code": "PKTL",
                        "imageURL": "https://nhathuocviet.vn/data/items/3817/pantoloc-20mg-1.jpg"
                    }
                """;

        MedicationDto expectedResponse = mapper.readValue(response, MedicationDto.class);

        loadInSampleDrone();

        BigInteger requestDroneSerial = BigInteger.valueOf(1234567890);
        List<MedicationDto> listResponse = droneService.retrieveDroneLoad(requestDroneSerial);
        MedicationDto actualResponse = listResponse.get(0);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getCode(), actualResponse.getCode());
        assertEquals(expectedResponse.getWeight(), actualResponse.getWeight());
        assertEquals(expectedResponse.getImageURL(), actualResponse.getImageURL());
    }

    private void loadInSampleDrone(){
        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567890),
                LIGHTWEIGHT,
                99.9
        );
        droneService.registerDrone(droneRequest);

        LoadMedicationRequestDto loadRequest = new LoadMedicationRequestDto();
        loadRequest.setDroneSerialNumber(BigInteger.valueOf(1234567890));
        loadRequest.setMedication(
                MedicationDto.builder()
                        .name("Paktal7")
                        .weight(195.0)
                        .code("PKTL")
                        .imageURL("https://nhathuocviet.vn/data/items/3817/pantoloc-20mg-1.jpg")
                        .build()
        );

        droneService.loadMedicine(loadRequest);
    }

    private void fillDroneDB(){
        DroneDto droneRequest = new DroneDto(
                BigInteger.valueOf(1234567890),
                LIGHTWEIGHT,
                99.9
        );
        droneService.registerDrone(droneRequest);

        droneRequest.setSerialNumber(BigInteger.valueOf(1234567891));
        droneService.registerDrone(droneRequest);
        droneRequest.setSerialNumber(BigInteger.valueOf(1234567892));
        droneService.registerDrone(droneRequest);
        droneRequest.setSerialNumber(BigInteger.valueOf(1234567893));
        droneService.registerDrone(droneRequest);
        droneRequest.setSerialNumber(BigInteger.valueOf(1234567894));
        droneService.registerDrone(droneRequest);
        droneRequest.setSerialNumber(BigInteger.valueOf(1234567895));
        droneService.registerDrone(droneRequest);
        droneRequest.setSerialNumber(BigInteger.valueOf(1234567896));
        droneService.registerDrone(droneRequest);
        droneRequest.setSerialNumber(BigInteger.valueOf(1234567897));
        droneService.registerDrone(droneRequest);
        droneRequest.setSerialNumber(BigInteger.valueOf(1234567898));
        droneService.registerDrone(droneRequest);
        droneRequest.setSerialNumber(BigInteger.valueOf(1234567899));
        droneService.registerDrone(droneRequest);
    }
}