package com.onclass.bootcamp.domain.usecase;

import com.onclass.bootcamp.domain.excepcion.BootcampException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampPage;
import com.onclass.bootcamp.domain.model.Capacidad;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.ICapacidadServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseTest {

    @Mock
    private IBootcampPersistencePort persistencePort;

    @Mock
    private ICapacidadServicePort capacidadServicePort;

    @InjectMocks
    private BootcampUseCase useCase;

    private List<Capacidad> capacidadesValidas() {
        return List.of(
                new Capacidad(3L, "Backend Developer", null),
                new Capacidad(4L, "Frontend Developer", null)
        );
    }

    private Bootcamp bootcampValido() {
        return new Bootcamp(null, "Bootcamp Java", "Descripción válida",
                LocalDate.of(2026, 6, 1), 90, capacidadesValidas());
    }

    @Test
    void registrar_exitoso() {
        Bootcamp bootcamp = bootcampValido();
        when(capacidadServicePort.existeCapacidad(anyLong())).thenReturn(Mono.just(true));
        when(persistencePort.existePorNombre("Bootcamp Java")).thenReturn(Mono.just(false));
        when(persistencePort.guardar(any())).thenReturn(Mono.just(
                new Bootcamp(1L, "Bootcamp Java", "Descripción válida",
                        LocalDate.of(2026, 6, 1), 90, capacidadesValidas())));

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectNextMatches(b -> b.getId() == 1L)
                .verifyComplete();
    }

    @Test
    void registrar_nombreVacio_lanzaError() {
        Bootcamp bootcamp = new Bootcamp(null, "", "Descripción",
                LocalDate.now(), 90, capacidadesValidas());

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_nombreNull_lanzaError() {
        Bootcamp bootcamp = new Bootcamp(null, null, "Descripción",
                LocalDate.now(), 90, capacidadesValidas());

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_nombreMayorA50Chars_lanzaError() {
        Bootcamp bootcamp = new Bootcamp(null, "A".repeat(51), "Descripción",
                LocalDate.now(), 90, capacidadesValidas());

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_descripcionVacia_lanzaError() {
        Bootcamp bootcamp = new Bootcamp(null, "Bootcamp", "",
                LocalDate.now(), 90, capacidadesValidas());

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_descripcionNull_lanzaError() {
        Bootcamp bootcamp = new Bootcamp(null, "Bootcamp", null,
                LocalDate.now(), 90, capacidadesValidas());

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_descripcionMayorA90Chars_lanzaError() {
        Bootcamp bootcamp = new Bootcamp(null, "Bootcamp", "A".repeat(91),
                LocalDate.now(), 90, capacidadesValidas());

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_fechaNull_lanzaError() {
        Bootcamp bootcamp = new Bootcamp(null, "Bootcamp", "Descripción",
                null, 90, capacidadesValidas());

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_duracionNull_lanzaError() {
        Bootcamp bootcamp = new Bootcamp(null, "Bootcamp", "Descripción",
                LocalDate.now(), null, capacidadesValidas());

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_sinCapacidades_lanzaError() {
        Bootcamp bootcamp = new Bootcamp(null, "Bootcamp", "Descripción",
                LocalDate.now(), 90, List.of());

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_masde4Capacidades_lanzaError() {
        List<Capacidad> capacidades = List.of(
                new Capacidad(1L, "Cap1", null),
                new Capacidad(2L, "Cap2", null),
                new Capacidad(3L, "Cap3", null),
                new Capacidad(4L, "Cap4", null),
                new Capacidad(5L, "Cap5", null)
        );
        Bootcamp bootcamp = new Bootcamp(null, "Bootcamp", "Descripción",
                LocalDate.now(), 90, capacidades);

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_capacidadesRepetidas_lanzaError() {
        List<Capacidad> capacidades = List.of(
                new Capacidad(3L, "Backend", null),
                new Capacidad(3L, "Backend", null)
        );
        Bootcamp bootcamp = new Bootcamp(null, "Bootcamp", "Descripción",
                LocalDate.now(), 90, capacidades);

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_capacidadNoExiste_lanzaError() {
        Bootcamp bootcamp = bootcampValido();
        when(capacidadServicePort.existeCapacidad(anyLong())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void registrar_nombreDuplicado_lanzaError() {
        Bootcamp bootcamp = bootcampValido();
        when(capacidadServicePort.existeCapacidad(anyLong())).thenReturn(Mono.just(true));
        when(persistencePort.existePorNombre("Bootcamp Java")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.registrar(bootcamp))
                .expectError(BootcampException.class)
                .verify();
    }


    @Test
    void listarPaginado_exitoso() {
        List<Bootcamp> bootcamps = List.of(
                new Bootcamp(1L, "Bootcamp Java", "Desc", LocalDate.now(), 90,
                        List.of(new Capacidad(3L, "Backend", null)))
        );
        BootcampPage page = new BootcampPage(bootcamps, 0, 1, 1L);

        when(persistencePort.listarPaginado(0, 10, "nombre", "asc"))
                .thenReturn(Mono.just(page));
        when(capacidadServicePort.obtenerCapacidad(anyLong()))
                .thenReturn(Mono.just(new Capacidad(3L, "Backend Developer", null)));

        StepVerifier.create(useCase.listarPaginado(0, 10, "nombre", "asc"))
                .expectNextMatches(p -> p.getTotalElementos() == 1L)
                .verifyComplete();
    }

    @Test
    void listarPaginado_parametrosNulos_usaDefecto() {
        List<Bootcamp> bootcamps = List.of(
                new Bootcamp(1L, "Bootcamp Java", "Desc", LocalDate.now(), 90,
                        List.of(new Capacidad(3L, "Backend", null)))
        );
        BootcampPage page = new BootcampPage(bootcamps, 0, 1, 1L);

        when(persistencePort.listarPaginado(0, 10, "nombre", "asc"))
                .thenReturn(Mono.just(page));
        when(capacidadServicePort.obtenerCapacidad(anyLong()))
                .thenReturn(Mono.just(new Capacidad(3L, "Backend Developer", null)));

        StepVerifier.create(useCase.listarPaginado(0, 10, null, null))
                .expectNextMatches(p -> p.getTotalElementos() == 1L)
                .verifyComplete();
    }

    @Test
    void listarPaginado_conParametrosValidos() {
        List<Bootcamp> bootcamps = List.of(
                new Bootcamp(1L, "Bootcamp Java", "Desc", LocalDate.now(), 90,
                        List.of(new Capacidad(3L, "Backend", null)))
        );
        BootcampPage page = new BootcampPage(bootcamps, 0, 1, 1L);

        when(persistencePort.listarPaginado(0, 10, "cantidadCapacidades", "desc"))
                .thenReturn(Mono.just(page));
        when(capacidadServicePort.obtenerCapacidad(anyLong()))
                .thenReturn(Mono.just(new Capacidad(3L, "Backend Developer", null)));

        StepVerifier.create(useCase.listarPaginado(0, 10, "cantidadCapacidades", "desc"))
                .expectNextMatches(p -> p.getTotalElementos() == 1L)
                .verifyComplete();
    }

    @Test
    void eliminar_exitoso() {
        Bootcamp bootcamp = new Bootcamp(1L, "Bootcamp Java", "Desc",
                LocalDate.now(), 90,
                List.of(new Capacidad(3L, "Backend", null)));
        when(persistencePort.buscarPorId(1L)).thenReturn(Mono.just(bootcamp));
        when(persistencePort.obtenerCapacidadesDeOtrosBootcamps(1L, List.of(3L)))
                .thenReturn(Flux.empty());
        when(persistencePort.eliminar(1L)).thenReturn(Mono.empty());
        when(capacidadServicePort.eliminarCapacidad(3L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.eliminar(1L))
                .verifyComplete();
    }

    @Test
    void eliminar_bootcampNoExiste_lanzaError() {
        when(persistencePort.buscarPorId(999L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.eliminar(999L))
                .expectError(BootcampException.class)
                .verify();
    }

    @Test
    void eliminar_capacidadEnOtrosBootcamps_noElimina() {
        Bootcamp bootcamp = new Bootcamp(1L, "Bootcamp Java", "Desc",
                LocalDate.now(), 90,
                List.of(new Capacidad(3L, "Backend", null)));
        when(persistencePort.buscarPorId(1L)).thenReturn(Mono.just(bootcamp));
        when(persistencePort.obtenerCapacidadesDeOtrosBootcamps(1L, List.of(3L)))
                .thenReturn(Flux.just(3L));
        when(persistencePort.eliminar(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.eliminar(1L))
                .verifyComplete();
    }

}
