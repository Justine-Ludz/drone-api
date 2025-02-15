package com.medication.drone.repository;

import com.medication.drone.repository.entity.Drone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface DroneRepository extends CrudRepository<Drone, Long> {
    Optional<Drone> findBySerialNumber(BigInteger serialNumber);
}
