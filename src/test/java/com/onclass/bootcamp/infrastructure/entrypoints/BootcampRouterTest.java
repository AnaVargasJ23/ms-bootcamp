package com.onclass.bootcamp.infrastructure.entrypoints;

import com.onclass.bootcamp.domain.api.IBootcampServicePort;
import com.onclass.bootcamp.domain.excepcion.BootcampException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampPage;
import com.onclass.bootcamp.domain.model.Capacidad;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampRequest;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampResponse;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.CapacidadIdRequest;
import com.onclass.bootcamp.infrastructure.entrypoints.handler.BootcampHandler;
import com.onclass.bootcamp.infrastructure.entrypoints.mapper.BootcampMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebFluxTest
@Import({BootcampRouter.class, BootcampHandler.class})
class BootcampRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IBootcampServicePort servicePort;

    @MockBean
    private BootcampMapper bootcampMapper;

    private BootcampRequest requestValido() {
        return new BootcampRequest(
                "Java Backend",
                "Bootcamp de desarrollo backend con Java",
                LocalDate.of(2026, 8, 1),
                90,
                List.of(new CapacidadIdRequest(3L))
        );
    }

    private Bootcamp bootcampDomain() {
        return new Bootcamp(
                1L,
                "Java Backend",
                "Bootcamp de desarrollo backend con Java",
                LocalDate.of(2026, 8, 1),
                90,
                List.of(new Capacidad(3L, "Backend Developer", null))
        );
    }

    private BootcampResponse bootcampResponse() {
        return new BootcampResponse(
                1L,
                "Java Backend",
                "Bootcamp de desarrollo backend con Java",
                LocalDate.of(2026, 8, 1),
                90,
                List.of()
        );
    }

    @Test
    void registrar_exitoso_retorna201() {
        when(bootcampMapper.toDomain(any(BootcampRequest.class))).thenReturn(bootcampDomain());
        when(servicePort.registrar(any())).thenReturn(Mono.just(bootcampDomain()));

        webTestClient.post()
                .uri("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.nombre").isEqualTo("Java Backend")
                .jsonPath("$.mensaje").isEqualTo("Bootcamp registrado exitosamente");
    }

    @Test
    void registrar_nombreDuplicado_retorna400() {
        when(bootcampMapper.toDomain(any(BootcampRequest.class))).thenReturn(bootcampDomain());
        when(servicePort.registrar(any())).thenReturn(
                Mono.error(new BootcampException("BOOT-001", "Ya existe un bootcamp con ese nombre")));

        webTestClient.post()
                .uri("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BOOT-001");
    }

    @Test
    void registrar_capacidadNoExiste_retorna400() {
        when(bootcampMapper.toDomain(any(BootcampRequest.class))).thenReturn(bootcampDomain());
        when(servicePort.registrar(any())).thenReturn(
                Mono.error(new BootcampException("BOOT-007", "La capacidad no existe")));

        webTestClient.post()
                .uri("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BOOT-007");
    }

    @Test
    void registrar_errorInterno_retorna500() {
        when(bootcampMapper.toDomain(any(BootcampRequest.class))).thenReturn(bootcampDomain());
        when(servicePort.registrar(any())).thenReturn(
                Mono.error(new RuntimeException("Error de BD")));

        webTestClient.post()
                .uri("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido())
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BOOT-500");
    }

    @Test
    void listarPaginado_retorna200() {
        BootcampPage page = new BootcampPage(List.of(bootcampDomain()), 0, 1, 1L);

        when(servicePort.listarPaginado(0, 10, "nombre", "asc"))
                .thenReturn(Mono.just(page));
        when(bootcampMapper.toResponse(any(Bootcamp.class))).thenReturn(bootcampResponse());

        webTestClient.get()
                .uri("/api/v1/bootcamps/paginado?page=0&size=10&ordenarPor=nombre&direccion=asc")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.paginaActual").isEqualTo(0)
                .jsonPath("$.totalElementos").isEqualTo(1);
    }

    @Test
    void listarPaginado_ordenadoPorCantidadCapacidades_retorna200() {
        BootcampPage page = new BootcampPage(List.of(bootcampDomain()), 0, 1, 1L);

        when(servicePort.listarPaginado(0, 5, "cantidadCapacidades", "desc"))
                .thenReturn(Mono.just(page));
        when(bootcampMapper.toResponse(any(Bootcamp.class))).thenReturn(bootcampResponse());

        webTestClient.get()
                .uri("/api/v1/bootcamps/paginado?page=0&size=5&ordenarPor=cantidadCapacidades&direccion=desc")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void listarPaginado_sinParametros_usaDefectos() {
        BootcampPage page = new BootcampPage(List.of(bootcampDomain()), 0, 1, 1L);

        when(servicePort.listarPaginado(0, 10, "nombre", "asc"))
                .thenReturn(Mono.just(page));
        when(bootcampMapper.toResponse(any(Bootcamp.class))).thenReturn(bootcampResponse());

        webTestClient.get()
                .uri("/api/v1/bootcamps/paginado")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void eliminar_exitoso_retorna200() {
        when(servicePort.eliminar(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/bootcamps/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Bootcamp eliminado exitosamente");
    }

    @Test
    void eliminar_noExiste_retorna404() {
        when(servicePort.eliminar(anyLong())).thenReturn(
                Mono.error(new BootcampException("BOOT-002", "Bootcamp no encontrado")));

        webTestClient.delete()
                .uri("/api/v1/bootcamps/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BOOT-002");
    }
}