
package com.freightfox.equationsolver.exception;

import java.util.UUID;

public class EquationNotFoundException extends RuntimeException {

    public EquationNotFoundException(UUID id) {
        super("Equation not found with ID: " + id);
    }
}
