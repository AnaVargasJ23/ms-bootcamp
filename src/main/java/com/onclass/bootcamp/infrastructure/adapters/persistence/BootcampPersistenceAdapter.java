package com.onclass.bootcamp.infrastructure.adapters.persistence;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampPage;
import com.onclass.bootcamp.domain.model.Capacidad;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampCapacidadEntity;
import com.onclass.bootcamp.infrastructure.adapters.persistence.mapper.BootcampEntityMapper;
import com.onclass.bootcamp.infrastructure.adapters.persistence.repository.BootcampCapacidadR2dbcRepository;
import com.onclass.bootcamp.infrastructure.adapters.persistence.repository.BootcampR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
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

    @Override
    public Mono<BootcampPage> listarPaginado(int pagina, int tamanio, String ordenarPor, String direccion) {
        int offset = pagina * tamanio;
        return bootcampRepository.count()
                .flatMap(total -> {
                    int totalPaginas = (int) Math.ceil((double) total / tamanio);
                    return bootcampRepository.findAll()
                            .flatMap(entity ->
                                    bootcampCapacidadRepository.findByBootcampId(entity.getId())
                                            .map(rel -> new Capacidad(rel.getCapacidadId(), null, null))
                                            .collectList()
                                            .map(capacidades -> {
                                                Bootcamp b = bootcampEntityMapper.toDomain(entity);
                                                b.setCapacidades(capacidades);
                                                return b;
                                            })
                            )
                            .sort(getComparator(ordenarPor, direccion))
                            .skip(offset)
                            .take(tamanio)
                            .collectList()
                            .map(bootcamps -> new BootcampPage(bootcamps, pagina, totalPaginas, total));
                });
    }

    private Comparator<Bootcamp> getComparator(String ordenarPor, String direccion) {
        Comparator<Bootcamp> comparator;
        if ("cantidadCapacidades".equalsIgnoreCase(ordenarPor)) {
            comparator = Comparator.comparingInt(b -> b.getCapacidades().size());
        } else {
            comparator = Comparator.comparing(b -> b.getNombre().toLowerCase());
        }
        if ("desc".equalsIgnoreCase(direccion)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }
    @Override
    public Mono<Bootcamp> buscarPorId(Long id) {
        return bootcampRepository.findById(id)
                .flatMap(entity ->
                        bootcampCapacidadRepository.findByBootcampId(entity.getId())
                                .map(rel -> new Capacidad(rel.getCapacidadId(), null, null))
                                .collectList()
                                .map(capacidades -> {
                                    Bootcamp b = bootcampEntityMapper.toDomain(entity);
                                    b.setCapacidades(capacidades);
                                    return b;
                                })
                );
    }

    @Override
    @Transactional
    public Mono<Void> eliminar(Long id) {
        return bootcampCapacidadRepository.deleteByBootcampId(id)
                .then(bootcampRepository.deleteById(id));
    }

    @Override
    public Flux<Long> obtenerCapacidadesDeOtrosBootcamps(Long bootcampId, List<Long> capacidadIds) {
        return bootcampCapacidadRepository.findAll()
                .filter(rel -> !rel.getBootcampId().equals(bootcampId)
                        && capacidadIds.contains(rel.getCapacidadId()))
                .map(BootcampCapacidadEntity::getCapacidadId)
                .distinct();
    }


}
