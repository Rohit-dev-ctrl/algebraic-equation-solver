
package com.freightfox.equationsolver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {

    private Instant timestamp;

    private int status;

    private String error;

    private String message;

    private String path;
}
