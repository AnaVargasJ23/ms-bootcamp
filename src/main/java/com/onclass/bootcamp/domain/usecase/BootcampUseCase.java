package com.onclass.bootcamp.domain.usecase;

import com.onclass.bootcamp.domain.api.IBootcampServicePort;
import com.onclass.bootcamp.domain.constants.BootcampConstants;
import com.onclass.bootcamp.domain.enums.BootcampErrorEnum;
import com.onclass.bootcamp.domain.excepcion.BootcampException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampPage;
import com.onclass.bootcamp.domain.model.Capacidad;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.ICapacidadServicePort;
import com.onclass.bootcamp.domain.spi.IReporteServicePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class BootcampUseCase implements IBootcampServicePort {

    private static final Logger log = Logger.getLogger(BootcampUseCase.class.getName());

    private final IBootcampPersistencePort persistencePort;
    private final ICapacidadServicePort capacidadServicePort;
    private final IReporteServicePort reporteServicePort;

    @Override
    public Mono<Bootcamp> registrar(Bootcamp bootcamp) {
        return validar(bootcamp)
                .flatMap(b -> validarCapacidadesExisten(b.getCapacidades()))
                .flatMap(ignored -> persistencePort.existePorNombre(bootcamp.getNombre()))
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.error(new BootcampException(
                                BootcampErrorEnum.NOMBRE_DUPLICADO.getCode(),
                                BootcampErrorEnum.NOMBRE_DUPLICADO.getMessage()));
                    }
                    return persistencePort.guardar(bootcamp)
                            .flatMap(saved -> enriquecerCapacidades(saved)
                                    .flatMap(enriched -> {
                                        reporteServicePort.enviarReporte(enriched);
                                        return Mono.just(enriched);
                                    }));
                });
    }

    @Override
    public Mono<BootcampPage> listarPaginado(int pagina, int tamanio, String ordenarPor, String direccion) {
        String ordenValido = (ordenarPor == null || ordenarPor.isBlank()) ? "nombre" : ordenarPor;
        String direccionValida = (direccion == null || direccion.isBlank()) ? "asc" : direccion;
        return persistencePort.listarPaginado(pagina, tamanio, ordenValido, direccionValida)
                .flatMap(page -> enriquecerBootcamps(page.getBootcamps())
                        .map(bootcamps -> new BootcampPage(
                                bootcamps,
                                page.getPaginaActual(),
                                page.getTotalPaginas(),
                                page.getTotalElementos()
                        )));
    }

    @Override
    public Mono<Void> eliminar(Long id) {
        return persistencePort.buscarPorId(id)
                .switchIfEmpty(Mono.error(new BootcampException(
                        BootcampErrorEnum.BOOTCAMP_NO_ENCONTRADO.getCode(),
                        BootcampErrorEnum.BOOTCAMP_NO_ENCONTRADO.getMessage())))
                .flatMap(bootcamp -> {
                    List<Long> capacidadIds = bootcamp.getCapacidades()
                            .stream()
                            .map(Capacidad::getId)
                            .toList();
                    log.info("Capacidades del bootcamp " + id + ": " + capacidadIds);
                    return persistencePort.obtenerCapacidadesDeOtrosBootcamps(id, capacidadIds)
                            .collectList()
                            .flatMap(capacidadesEnOtros -> {
                                log.info("Capacidades en otros bootcamps: " + capacidadesEnOtros);
                                List<Long> capacidadesAEliminar = capacidadIds.stream()
                                        .filter(capId -> !capacidadesEnOtros.contains(capId))
                                        .toList();
                                log.info("Capacidades a eliminar: " + capacidadesAEliminar);
                                return persistencePort.eliminar(id)
                                        .then(Flux.fromIterable(capacidadesAEliminar)
                                                .flatMap(capacidadServicePort::eliminarCapacidad)
                                                .then());
                            });
                });
    }

    @Override
    public Mono<Bootcamp> buscarPorId(Long id) {
        return persistencePort.buscarPorId(id);
    }

    private Mono<List<Bootcamp>> enriquecerBootcamps(List<Bootcamp> bootcamps) {
        return Flux.fromIterable(bootcamps)
                .concatMap(this::enriquecerCapacidades)
                .collectList();
    }

    private Mono<Bootcamp> enriquecerCapacidades(Bootcamp bootcamp) {
        return Flux.fromIterable(bootcamp.getCapacidades())
                .concatMap(c -> capacidadServicePort.obtenerCapacidad(c.getId()))
                .collectList()
                .map(capacidades -> {
                    bootcamp.setCapacidades(capacidades);
                    return bootcamp;
                });
    }

    private Mono<Bootcamp> validar(Bootcamp bootcamp) {
        if (bootcamp.getNombre() == null || bootcamp.getNombre().isBlank()) {
            return Mono.error(new BootcampException(
                    BootcampErrorEnum.NOMBRE_OBLIGATORIO.getCode(),
                    BootcampErrorEnum.NOMBRE_OBLIGATORIO.getMessage()));
        }
        if (bootcamp.getNombre().length() > BootcampConstants.NOMBRE_MAX_LENGTH) {
            return Mono.error(new BootcampException(
                    BootcampErrorEnum.NOMBRE_MAX_50.getCode(),
                    BootcampErrorEnum.NOMBRE_MAX_50.getMessage()));
        }
        if (bootcamp.getDescripcion() == null || bootcamp.getDescripcion().isBlank()) {
            return Mono.error(new BootcampException(
                    BootcampErrorEnum.DESCRIPCION_OBLIGATORIA.getCode(),
                    BootcampErrorEnum.DESCRIPCION_OBLIGATORIA.getMessage()));
        }
        if (bootcamp.getDescripcion().length() > BootcampConstants.DESCRIPCION_MAX_LENGTH) {
            return Mono.error(new BootcampException(
                    BootcampErrorEnum.DESCRIPCION_MAX_90.getCode(),
                    BootcampErrorEnum.DESCRIPCION_MAX_90.getMessage()));
        }
        if (bootcamp.getFechaLanzamiento() == null) {
            return Mono.error(new BootcampException(
                    BootcampErrorEnum.FECHA_OBLIGATORIA.getCode(),
                    BootcampErrorEnum.FECHA_OBLIGATORIA.getMessage()));
        }
        if (bootcamp.getDuracion() == null) {
            return Mono.error(new BootcampException(
                    BootcampErrorEnum.DURACION_OBLIGATORIA.getCode(),
                    BootcampErrorEnum.DURACION_OBLIGATORIA.getMessage()));
        }
        if (bootcamp.getCapacidades() == null || bootcamp.getCapacidades().size() < BootcampConstants.CAPACIDADES_MIN) {
            return Mono.error(new BootcampException(
                    BootcampErrorEnum.CAPACIDADES_MIN_1.getCode(),
                    BootcampErrorEnum.CAPACIDADES_MIN_1.getMessage()));
        }
        if (bootcamp.getCapacidades().size() > BootcampConstants.CAPACIDADES_MAX) {
            return Mono.error(new BootcampException(
                    BootcampErrorEnum.CAPACIDADES_MAX_4.getCode(),
                    BootcampErrorEnum.CAPACIDADES_MAX_4.getMessage()));
        }
        List<Long> ids = bootcamp.getCapacidades().stream().map(Capacidad::getId).toList();
        Set<Long> idsUnicos = new HashSet<>(ids);
        if (idsUnicos.size() != ids.size()) {
            return Mono.error(new BootcampException(
                    BootcampErrorEnum.CAPACIDADES_REPETIDAS.getCode(),
                    BootcampErrorEnum.CAPACIDADES_REPETIDAS.getMessage()));
        }
        return Mono.just(bootcamp);
    }

    private Mono<Boolean> validarCapacidadesExisten(List<Capacidad> capacidades) {
        return Flux.fromIterable(capacidades)
                .flatMap(c -> capacidadServicePort.existeCapacidad(c.getId())
                        .flatMap(existe -> {
                            if (!existe) {
                                return Mono.error(new BootcampException(
                                        BootcampErrorEnum.CAPACIDAD_NO_EXISTE.getCode(),
                                        BootcampErrorEnum.CAPACIDAD_NO_EXISTE.getMessage()));
                            }
                            return Mono.just(existe);
                        }))
                .all(Boolean::booleanValue);
    }
}
