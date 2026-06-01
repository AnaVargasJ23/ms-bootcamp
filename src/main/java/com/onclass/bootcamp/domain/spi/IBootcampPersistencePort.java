package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampPage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IBootcampPersistencePort {
    Mono<Bootcamp> guardar(Bootcamp bootcamp);
    Mono<Boolean> existePorNombre(String nombre);
    Mono<BootcampPage> listarPaginado(int pagina, int tamanio, String ordenarPor, String direccion);
    Mono<Bootcamp> buscarPorId(Long id);
    Mono<Void> eliminar(Long id);
    Flux<Long> obtenerCapacidadesDeOtrosBootcamps(Long bootcampId, List<Long> capacidadIds);
}
