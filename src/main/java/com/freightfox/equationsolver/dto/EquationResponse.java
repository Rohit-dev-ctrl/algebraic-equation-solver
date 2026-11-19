package com.freightfox.equationsolver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EquationResponse {

    private String equationId;
    private String equation;
}