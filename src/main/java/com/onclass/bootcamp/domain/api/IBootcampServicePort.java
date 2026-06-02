package com.onclass.bootcamp.domain.api;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampPage;
import reactor.core.publisher.Mono;

public interface IBootcampServicePort {
    Mono<Bootcamp> registrar(Bootcamp bootcamp);
    Mono<BootcampPage> listarPaginado(int pagina, int tamanio, String ordenarPor, String direccion);
    Mono<Void> eliminar(Long id);
    Mono<Bootcamp> buscarPorId(Long id);
}
