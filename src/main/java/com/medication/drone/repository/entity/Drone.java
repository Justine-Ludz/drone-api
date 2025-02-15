package com.medication.drone.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.medication.drone.constants.DroneModelEnum;
import com.medication.drone.constants.DroneState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "drone")
public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, precision = 100, scale = 0)
    private BigInteger serialNumber;

    @Column(name = "model", nullable = false)
    @Enumerated(EnumType.STRING)
    private DroneModelEnum model;

    @Min(100)
    @Max(1000)
    @Column(name = "weight_limit", nullable = false)
    private Double weightLimit;

    @Column(name = "battery_capacity", nullable = false)
    @Max(100)
    @Min(0)
    private Double batteryCapacity;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private DroneState state;

    @OneToMany(mappedBy = "drone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Medicine> medicine;
}
