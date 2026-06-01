package com.onclass.bootcamp.infrastructure.adapters.http;

import com.onclass.bootcamp.domain.constants.BootcampConstants;
import com.onclass.bootcamp.domain.spi.ICapacidadServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CapacidadHttpAdapter implements ICapacidadServicePort {

    private final WebClient webClient;

    @Override
    public Mono<Boolean> existeCapacidad(Long id) {
        return webClient.get()
                .uri(BootcampConstants.CAPACIDAD_BASE_URL +
                        BootcampConstants.CAPACIDAD_BUSCAR_ENDPOINT, id)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorResume(e -> {
                    log.error("Error verificando capacidad {}: {}", id, e.getMessage());
                    return Mono.just(false);
                });
    }
}
