
package com.freightfox.equationsolver.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "FreightFox Equation Solver API",
                version = "1.0.0",
                description = "REST API for storing, retrieving and evaluating algebraic equations."
        )
)
public class OpenApiConfig {
}
