package com.onclass.bootcamp.domain.api;

import com.onclass.bootcamp.domain.model.Bootcamp;
import reactor.core.publisher.Mono;

public interface IBootcampServicePort {
    Mono<Bootcamp> registrar(Bootcamp bootcamp);
}
