
package com.freightfox.equationsolver.repository;

import com.freightfox.equationsolver.model.Equation;
import com.freightfox.equationsolver.model.ExpressionNode;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EquationRepositoryTest {

    private final EquationRepository repository =
            new EquationRepository();

    @Test
    void shouldSaveAndRetrieveEquation() {

        UUID id = UUID.randomUUID();

        Equation equation = new Equation(
                id,
                "3x + 2y",
                new ExpressionNode("+"),
                Instant.now()
        );

        repository.save(equation);

        assertTrue(repository.existsById(id));

        assertEquals(
                equation,
                repository.findById(id).orElseThrow()
        );
    }

    @Test
    void shouldReturnAllEquations() {

        Equation equation1 = new Equation(
                UUID.randomUUID(),
                "x + 2",
                new ExpressionNode("+"),
                Instant.now()
        );

        Equation equation2 = new Equation(
                UUID.randomUUID(),
                "y * 3",
                new ExpressionNode("*"),
                Instant.now()
        );

        repository.save(equation1);
        repository.save(equation2);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldDeleteEquation() {

        UUID id = UUID.randomUUID();

        Equation equation = new Equation(
                id,
                "x + 2",
                new ExpressionNode("+"),
                Instant.now()
        );

        repository.save(equation);

        assertTrue(repository.deleteById(id));
        assertFalse(repository.existsById(id));
    }

    @Test
    void shouldReturnEmptyForUnknownId() {

        assertTrue(
                repository.findById(UUID.randomUUID()).isEmpty()
        );
    }
}
