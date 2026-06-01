package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.model.Capacidad;
import reactor.core.publisher.Mono;

public interface ICapacidadServicePort {
    Mono<Boolean> existeCapacidad(Long id);
    Mono<Capacidad> obtenerCapacidad(Long id);
}
