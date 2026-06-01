package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.model.Bootcamp;
import reactor.core.publisher.Mono;

public interface IBootcampPersistencePort {
    Mono<Bootcamp> guardar(Bootcamp bootcamp);
    Mono<Boolean> existePorNombre(String nombre);
}
