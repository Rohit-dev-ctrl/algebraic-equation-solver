package com.freightfox.equationsolver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class EvaluateEquationResponse {

    private String equationId;
    private String equation;
    private Map<String, Double> variables;
    private double result;
}