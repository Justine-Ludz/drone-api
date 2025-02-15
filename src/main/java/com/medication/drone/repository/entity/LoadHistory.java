package com.medication.drone.repository.entity;

import com.medication.drone.constants.LoadStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoadHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoadStatus status;
}
