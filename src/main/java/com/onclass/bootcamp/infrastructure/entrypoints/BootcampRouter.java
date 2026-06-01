package com.onclass.bootcamp.infrastructure.entrypoints;

import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampRequest;
import com.onclass.bootcamp.infrastructure.entrypoints.handler.BootcampHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Tag(name = "Bootcamp", description = "Gestión de bootcamps del sistema On-Class")
public class BootcampRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/bootcamps",
                    method = RequestMethod.POST,
                    beanClass = BootcampHandler.class,
                    beanMethod = "registrar",
                    operation = @Operation(
                            operationId = "registrarBootcamp",
                            summary = "Registrar un nuevo bootcamp (Requiere rol ADMIN)",
                            tags = {"Bootcamp"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = BootcampRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Bootcamp creado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos o reglas de negocio violadas")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/bootcamps", handler::registrar)
                .build();
    }
}
