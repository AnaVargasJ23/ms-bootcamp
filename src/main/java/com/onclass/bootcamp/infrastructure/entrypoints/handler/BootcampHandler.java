package com.onclass.bootcamp.infrastructure.entrypoints.handler;

import com.onclass.bootcamp.domain.api.IBootcampServicePort;
import com.onclass.bootcamp.domain.excepcion.BootcampException;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampPageResponse;
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

    public Mono<ServerResponse> listarPaginado(ServerRequest request) {
        int pagina = Integer.parseInt(request.queryParam("page").orElse("0"));
        int tamanio = Integer.parseInt(request.queryParam("size").orElse("10"));
        String ordenarPor = request.queryParam("ordenarPor").orElse("nombre");
        String direccion = request.queryParam("direccion").orElse("asc");

        return bootcampServicePort.listarPaginado(pagina, tamanio, ordenarPor, direccion)
                .map(page -> new BootcampPageResponse(
                        page.getBootcamps().stream()
                                .map(bootcampMapper::toResponse)
                                .toList(),
                        page.getPaginaActual(),
                        page.getTotalPaginas(),
                        page.getTotalElementos()
                ))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(BootcampException.class, e ->
                        ServerResponse.badRequest().bodyValue(ErrorDTO.builder()
                                .code(e.getCode())
                                .message(e.getMessage())
                                .build()));
    }
}
