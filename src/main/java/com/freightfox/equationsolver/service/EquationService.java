
package com.freightfox.equationsolver.service;

import com.freightfox.equationsolver.dto.CreateEquationRequest;
import com.freightfox.equationsolver.dto.EquationListResponse;
import com.freightfox.equationsolver.dto.EquationResponse;
import com.freightfox.equationsolver.dto.EvaluateEquationRequest;
import com.freightfox.equationsolver.dto.EvaluateEquationResponse;
import com.freightfox.equationsolver.dto.StoreEquationResponse;
import com.freightfox.equationsolver.exception.EquationNotFoundException;
import com.freightfox.equationsolver.model.Equation;
import com.freightfox.equationsolver.model.ExpressionNode;
import com.freightfox.equationsolver.parser.ExpressionTreeBuilder;
import com.freightfox.equationsolver.parser.ExpressionTreeFormatter;
import com.freightfox.equationsolver.parser.PostfixConverter;
import com.freightfox.equationsolver.parser.Tokenizer;
import com.freightfox.equationsolver.repository.EquationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquationService {

    private final Tokenizer tokenizer;
    private final PostfixConverter postfixConverter;
    private final ExpressionTreeBuilder treeBuilder;
    private final ExpressionTreeFormatter treeFormatter;
    private final ExpressionEvaluator evaluator;
    private final EquationRepository repository;

    // Store an algebraic equation
    public StoreEquationResponse createEquation(
            CreateEquationRequest request) {

        if (request == null
                || request.getEquation() == null
                || request.getEquation().isBlank()) {

            throw new IllegalArgumentException(
                    "Equation cannot be empty"
            );
        }

        String expression = request.getEquation();

        // Step 1: Tokenize the equation
        List<String> tokens = tokenizer.tokenize(expression);

        // Step 2: Convert infix tokens into postfix notation
        List<String> postfix = postfixConverter.convert(tokens);

        // Step 3: Construct the expression tree
        ExpressionNode root = treeBuilder.build(postfix);

        // Step 4: Create the equation object
        Equation equation = new Equation(
                UUID.randomUUID(),
                expression,
                root,
                Instant.now()
        );

        // Step 5: Store the equation in memory
        repository.save(equation);

        return new StoreEquationResponse(
                "Equation stored successfully",
                equation.getId().toString()
        );
    }

    // Retrieve all stored equations
    public EquationListResponse getAllEquations() {

        List<EquationResponse> equations = repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new EquationListResponse(equations);
    }

    // Retrieve an equation using its ID
    public EquationResponse getEquationById(UUID id) {

        Equation equation = repository.findById(id)
                .orElseThrow(() ->
                        new EquationNotFoundException(id)
                );

        return mapToResponse(equation);
    }

    // Evaluate a stored equation
    public EvaluateEquationResponse evaluateEquation(
            UUID id,
            EvaluateEquationRequest request) {

        if (request == null || request.getVariables() == null) {

            throw new IllegalArgumentException(
                    "Variable values cannot be null"
            );
        }

        Equation equation = repository.findById(id)
                .orElseThrow(() ->
                        new EquationNotFoundException(id)
                );

        Map<String, Double> variables = request.getVariables();

        // Validate variable names and values
        for (Map.Entry<String, Double> entry : variables.entrySet()) {

            String variable = entry.getKey();
            Double value = entry.getValue();

            if (variable == null || variable.isBlank()) {

                throw new IllegalArgumentException(
                        "Variable name cannot be empty"
                );
            }

            if (value == null || !Double.isFinite(value)) {

                throw new IllegalArgumentException(
                        "Invalid value for variable: " + variable
                );
            }
        }

        // Evaluate directly using the stored expression tree
        double result = evaluator.evaluate(
                equation.getRoot(),
                variables
        );

        // Reconstruct the equation by traversing its tree
        String formattedEquation = treeFormatter.format(
                equation.getRoot()
        );

        return new EvaluateEquationResponse(
                equation.getId().toString(),
                formattedEquation,
                Map.copyOf(variables),
                result
        );
    }

    // Convert the expression tree into an API response
    private EquationResponse mapToResponse(Equation equation) {

        String reconstructedEquation = treeFormatter.format(
                equation.getRoot()
        );

        return new EquationResponse(
                equation.getId().toString(),
                reconstructedEquation
        );
    }
}
