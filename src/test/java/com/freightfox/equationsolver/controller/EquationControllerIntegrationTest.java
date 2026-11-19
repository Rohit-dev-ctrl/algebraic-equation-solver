
package com.freightfox.equationsolver.controller;

import com.freightfox.equationsolver.repository.EquationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EquationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private EquationRepository equationRepository;

    // Clear stored equations before every test
    @BeforeEach
    void clearRepository() {
        equationRepository.findAll()
                .forEach(equation ->
                        equationRepository.deleteById(equation.getId())
                );
    }

    // Helper method to create an equation and return its ID
    private String createEquation(String equation) throws Exception {

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("equation", equation)
        );

        MvcResult result = mockMvc.perform(post("/api/equations/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        return response.get("equationId").asText();
    }

    @Test
    void shouldCreateEquation() throws Exception {

        String requestBody = """
                {
                    "equation": "3x + 2y - z"
                }
                """;

        mockMvc.perform(post("/api/equations/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Equation stored successfully"))
                .andExpect(jsonPath("$.equationId").exists());
    }

    @Test
    void shouldRetrieveAllEquations() throws Exception {

        createEquation("x + 10");

        mockMvc.perform(get("/api/equations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equations").isArray())
                .andExpect(jsonPath("$.equations.length()").value(1))
                .andExpect(jsonPath("$.equations[0].equationId").exists())
                .andExpect(jsonPath("$.equations[0].equation").value("(x + 10)"));
    }

    @Test
    void shouldRetrieveEquationById() throws Exception {

        String id = createEquation("x + 5");

        mockMvc.perform(get("/api/equations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equationId").value(id))
                .andExpect(jsonPath("$.equation").value("(x + 5)"));
    }

    @Test
    void shouldEvaluateEquation() throws Exception {

        String id = createEquation("3x + 2y - z");

        String requestBody = """
                {
                    "variables": {
                        "x": 2,
                        "y": 3,
                        "z": 1
                    }
                }
                """;

        mockMvc.perform(post("/api/equations/{id}/evaluate", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equationId").value(id))
                .andExpect(jsonPath("$.result").value(11.0))
                .andExpect(jsonPath("$.variables.x").value(2.0))
                .andExpect(jsonPath("$.variables.y").value(3.0))
                .andExpect(jsonPath("$.variables.z").value(1.0));
    }

    @Test
    void shouldReturn400ForInvalidExpression() throws Exception {

        String requestBody = """
                {
                    "equation": "3x + @"
                }
                """;

        mockMvc.perform(post("/api/equations/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForDivisionByZero() throws Exception {

        String id = createEquation("10 / 0");

        String requestBody = """
                {
                    "variables": {}
                }
                """;

        mockMvc.perform(post("/api/equations/{id}/evaluate", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
