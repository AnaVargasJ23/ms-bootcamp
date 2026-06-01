package com.onclass.bootcamp.infrastructure.adapters.persistence;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.Capacidad;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampCapacidadEntity;
import com.onclass.bootcamp.infrastructure.adapters.persistence.mapper.BootcampEntityMapper;
import com.onclass.bootcamp.infrastructure.adapters.persistence.repository.BootcampCapacidadR2dbcRepository;
import com.onclass.bootcamp.infrastructure.adapters.persistence.repository.BootcampR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BootcampPersistenceAdapter implements IBootcampPersistencePort {

    private final BootcampR2dbcRepository bootcampRepository;
    private final BootcampCapacidadR2dbcRepository bootcampCapacidadRepository;
    private final BootcampEntityMapper bootcampEntityMapper;

    @Override
    @Transactional
    public Mono<Bootcamp> guardar(Bootcamp bootcamp) {
        return bootcampRepository.save(bootcampEntityMapper.toEntity(bootcamp))
                .flatMap(savedEntity -> {
                    List<BootcampCapacidadEntity> relaciones = bootcamp.getCapacidades()
                            .stream()
                            .map(c -> new BootcampCapacidadEntity(savedEntity.getId(), c.getId()))
                            .toList();
                    return bootcampCapacidadRepository.saveAll(relaciones)
                            .then(Mono.just(bootcampEntityMapper.toDomain(savedEntity)))
                            .map(b -> {
                                b.setCapacidades(bootcamp.getCapacidades());
                                return b;
                            });
                });
    }

    @Override
    public Mono<Boolean> existePorNombre(String nombre) {
        return bootcampRepository.existsByNombre(nombre);
    }
}
