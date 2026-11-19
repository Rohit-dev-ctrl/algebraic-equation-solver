
package com.freightfox.equationsolver.service;

import com.freightfox.equationsolver.dto.CreateEquationRequest;
import com.freightfox.equationsolver.dto.EquationListResponse;
import com.freightfox.equationsolver.dto.EquationResponse;
import com.freightfox.equationsolver.dto.EvaluateEquationRequest;
import com.freightfox.equationsolver.dto.EvaluateEquationResponse;
import com.freightfox.equationsolver.dto.StoreEquationResponse;
import com.freightfox.equationsolver.exception.EquationNotFoundException;
import com.freightfox.equationsolver.parser.ExpressionTreeBuilder;
import com.freightfox.equationsolver.parser.ExpressionTreeFormatter;
import com.freightfox.equationsolver.parser.PostfixConverter;
import com.freightfox.equationsolver.parser.Tokenizer;
import com.freightfox.equationsolver.repository.EquationRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EquationServiceTest {

    private final EquationRepository repository =
            new EquationRepository();

    private final EquationService service =
            new EquationService(
                    new Tokenizer(),
                    new PostfixConverter(),
                    new ExpressionTreeBuilder(),
                    new ExpressionTreeFormatter(),
                    new ExpressionEvaluator(),
                    repository
            );

    @Test
    void shouldCreateAndStoreEquation() {

        CreateEquationRequest request =
                new CreateEquationRequest();

        request.setEquation("3x + 2y - z");

        StoreEquationResponse response =
                service.createEquation(request);

        assertNotNull(response.getEquationId());

        assertEquals(
                "Equation stored successfully",
                response.getMessage()
        );

        UUID id = UUID.fromString(response.getEquationId());

        assertTrue(repository.existsById(id));
    }

    @Test
    void shouldRetrieveAllEquations() {

        CreateEquationRequest request =
                new CreateEquationRequest();

        request.setEquation("x + 10");

        service.createEquation(request);

        EquationListResponse response =
                service.getAllEquations();

        assertEquals(1, response.getEquations().size());

        assertEquals(
                "(x + 10)",
                response.getEquations().get(0).getEquation()
        );
    }

    @Test
    void shouldRetrieveEquationById() {

        CreateEquationRequest request =
                new CreateEquationRequest();

        request.setEquation("x + 5");

        StoreEquationResponse created =
                service.createEquation(request);

        UUID id = UUID.fromString(created.getEquationId());

        EquationResponse retrieved =
                service.getEquationById(id);

        assertEquals(
                created.getEquationId(),
                retrieved.getEquationId()
        );

        assertEquals(
                "(x + 5)",
                retrieved.getEquation()
        );
    }

    @Test
    void shouldEvaluateStoredEquation() {

        CreateEquationRequest createRequest =
                new CreateEquationRequest();

        createRequest.setEquation("3x + 2y - z");

        StoreEquationResponse equation =
                service.createEquation(createRequest);

        EvaluateEquationRequest evaluateRequest =
                new EvaluateEquationRequest();

        evaluateRequest.setVariables(Map.of(
                "x", 2.0,
                "y", 3.0,
                "z", 1.0
        ));

        EvaluateEquationResponse response =
                service.evaluateEquation(
                        UUID.fromString(equation.getEquationId()),
                        evaluateRequest
                );

        assertEquals(11.0, response.getResult(), 0.0001);

        assertEquals(
                equation.getEquationId(),
                response.getEquationId()
        );

        assertEquals(
                Map.of("x", 2.0, "y", 3.0, "z", 1.0),
                response.getVariables()
        );
    }

    @Test
    void shouldRejectUnknownEquationId() {

        UUID unknownId = UUID.randomUUID();

        assertThrows(
                EquationNotFoundException.class,
                () -> service.getEquationById(unknownId)
        );
    }

    @Test
    void shouldRejectEmptyExpression() {

        CreateEquationRequest request =
                new CreateEquationRequest();

        request.setEquation("   ");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createEquation(request)
        );
    }
}
