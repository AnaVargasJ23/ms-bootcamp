package com.onclass.bootcamp.domain.spi;

import reactor.core.publisher.Mono;

public interface ICapacidadServicePort {
    Mono<Boolean> existeCapacidad(Long id);
}
