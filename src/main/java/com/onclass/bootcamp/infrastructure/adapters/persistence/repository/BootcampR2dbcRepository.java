package com.onclass.bootcamp.infrastructure.adapters.persistence.repository;

import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BootcampR2dbcRepository extends ReactiveCrudRepository<BootcampEntity, Long> {
    Mono<Boolean> existsByNombre(String nombre);
}
