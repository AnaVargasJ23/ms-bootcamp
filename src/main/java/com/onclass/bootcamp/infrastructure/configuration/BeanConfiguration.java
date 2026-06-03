package com.onclass.bootcamp.infrastructure.configuration;

import com.onclass.bootcamp.domain.api.IBootcampServicePort;
import com.onclass.bootcamp.domain.spi.IBootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.ICapacidadServicePort;
import com.onclass.bootcamp.domain.spi.IReporteServicePort;
import com.onclass.bootcamp.domain.usecase.BootcampUseCase;
import com.onclass.bootcamp.domain.constants.BootcampConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(BootcampConstants.CAPACIDAD_BASE_URL)
                .build();
    }

    @Bean
    public IBootcampServicePort bootcampServicePort(
            IBootcampPersistencePort persistencePort,
            ICapacidadServicePort capacidadServicePort,
            IReporteServicePort reporteServicePort) {
        return new BootcampUseCase(persistencePort, capacidadServicePort,reporteServicePort);
    }
}
