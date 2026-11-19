package com.freightfox.equationsolver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EvaluateEquationRequest {

    @NotNull(message = "Variables cannot be null")
    private Map<String, Double> variables;
}