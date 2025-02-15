package com.medication.drone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MedicationDto {
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "can only accept letters, numbers, -, _")
    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "Weight cannot be null")
    private Double weight;

    @Pattern(regexp = "^[A-Z0-9_]+$", message = "can only accept uppercase letters, underscores and numbers")
    @NotNull(message = "Code cannot be null")
    @NotBlank(message = "Code cannot be blank")
    private String code;

    @NotNull(message = "imageURL cannot be null")
    private String imageURL;
}
