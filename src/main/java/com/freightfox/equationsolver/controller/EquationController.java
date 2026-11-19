
package com.freightfox.equationsolver.controller;

import com.freightfox.equationsolver.dto.CreateEquationRequest;
import com.freightfox.equationsolver.dto.EquationListResponse;
import com.freightfox.equationsolver.dto.EquationResponse;
import com.freightfox.equationsolver.dto.EvaluateEquationRequest;
import com.freightfox.equationsolver.dto.EvaluateEquationResponse;
import com.freightfox.equationsolver.dto.StoreEquationResponse;
import com.freightfox.equationsolver.service.EquationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/equations")
@RequiredArgsConstructor
@Tag(
        name = "Equation Management",
        description = "APIs for storing, retrieving and evaluating equations"
)
public class EquationController {

    private final EquationService equationService;

    // Store an equation
    @PostMapping("/store")
    @Operation(summary = "Store an algebraic equation")
    public ResponseEntity<StoreEquationResponse> createEquation(
            @Valid @RequestBody CreateEquationRequest request) {

        StoreEquationResponse response =
                equationService.createEquation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Retrieve all equations
    @GetMapping
    @Operation(summary = "Retrieve all stored equations")
    public ResponseEntity<EquationListResponse> getAllEquations() {

        return ResponseEntity.ok(
                equationService.getAllEquations()
        );
    }

    // Retrieve an equation by ID
    @GetMapping("/{id}")
    @Operation(summary = "Retrieve an equation by ID")
    public ResponseEntity<EquationResponse> getEquationById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                equationService.getEquationById(id)
        );
    }

    // Evaluate an equation
    @PostMapping("/{id}/evaluate")
    @Operation(summary = "Evaluate a stored equation")
    public ResponseEntity<EvaluateEquationResponse> evaluateEquation(
            @PathVariable UUID id,
            @Valid @RequestBody EvaluateEquationRequest request) {

        EvaluateEquationResponse response =
                equationService.evaluateEquation(id, request);

        return ResponseEntity.ok(response);
    }
}
