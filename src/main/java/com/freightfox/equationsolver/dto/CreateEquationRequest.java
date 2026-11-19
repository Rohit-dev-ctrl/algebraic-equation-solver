package com.freightfox.equationsolver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEquationRequest {

    @NotBlank(message = "Equation cannot be empty")
    private String equation;
}