package com.onclass.bootcamp.infrastructure.entrypoints.handler;

import com.onclass.bootcamp.domain.api.IBootcampServicePort;
import com.onclass.bootcamp.domain.excepcion.BootcampException;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampRegistradoResponse;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampRequest;
import com.onclass.bootcamp.infrastructure.entrypoints.mapper.BootcampMapper;
import com.onclass.bootcamp.infrastructure.entrypoints.util.ErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootcampHandler {

    private final IBootcampServicePort bootcampServicePort;
    private final BootcampMapper bootcampMapper;

    public Mono<ServerResponse> registrar(ServerRequest request) {
        return request.bodyToMono(BootcampRequest.class)
                .map(bootcampMapper::toDomain)
                .flatMap(bootcampServicePort::registrar)
                .flatMap(saved -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(BootcampRegistradoResponse.builder()
                                .id(saved.getId())
                                .nombre(saved.getNombre())
                                .mensaje("Bootcamp registrado exitosamente")
                                .build()))
                .onErrorResume(BootcampException.class, e -> {
                    log.error("Error de negocio: {}", e.getMessage());
                    return ServerResponse
                            .status(HttpStatus.BAD_REQUEST)
                            .bodyValue(ErrorDTO.builder()
                                    .code(e.getCode())
                                    .message(e.getMessage())
                                    .build());
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Error inesperado: {}", e.getMessage());
                    return ServerResponse
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue(ErrorDTO.builder()
                                    .code("BOOT-500")
                                    .message("Error interno del servidor")
                                    .build());
                });
    }
}