package com.onclass.bootcamp.infrastructure.adapters.http;

import com.onclass.bootcamp.domain.constants.BootcampConstants;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.spi.IReporteServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReporteHttpAdapter implements IReporteServicePort {
    private final WebClient webClient;

    public void enviarReporte(Bootcamp bootcamp) {
        Map<String, Object> reporte = Map.of(
                "bootcampId", bootcamp.getId(),
                "nombre", bootcamp.getNombre(),
                "descripcion", bootcamp.getDescripcion(),
                "fechaLanzamiento", bootcamp.getFechaLanzamiento().toString(),
                "duracion", bootcamp.getDuracion(),
                "capacidades", bootcamp.getCapacidades(),
                "cantidadCapacidades", bootcamp.getCapacidades().size(),
                "cantidadTecnologias", bootcamp.getCapacidades().stream()
                        .mapToInt(c -> c.getTecnologias() != null ? c.getTecnologias().size() : 0)
                        .sum(),
                "cantidadPersonas", 0
        );

        webClient.post()
                .uri(BootcampConstants.REPORTE_BASE_URL + BootcampConstants.REPORTE_ENDPOINT)
                .bodyValue(reporte)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(r -> log.info("Reporte enviado para bootcamp {}", bootcamp.getId()))
                .doOnError(e -> log.error("Error enviando reporte: {}", e.getMessage()))
                .subscribe();
    }
}
