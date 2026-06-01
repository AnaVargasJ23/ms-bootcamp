package com.onclass.bootcamp.infrastructure.adapters.persistence.repository;

import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampCapacidadEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BootcampCapacidadR2dbcRepository extends ReactiveCrudRepository<BootcampCapacidadEntity, Long> {
    Flux<BootcampCapacidadEntity> findByBootcampId(Long bootcampId);
}
