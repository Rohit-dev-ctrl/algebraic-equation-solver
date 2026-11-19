package com.freightfox.equationsolver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EquationListResponse {

    private List<EquationResponse> equations;
}