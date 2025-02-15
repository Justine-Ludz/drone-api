package com.medication.drone.constants;

import lombok.Getter;

@Getter
public enum DroneModelWeightSpec {
    LIGHTWEIGHT(200),
    MIDDLEWEIGHT(400),
    CRUISERWEIGHT(600),
    HEAVYWEIGHT(1000);

    private final double weight;

    DroneModelWeightSpec(double weight) {
        this.weight = weight;
    }
}
