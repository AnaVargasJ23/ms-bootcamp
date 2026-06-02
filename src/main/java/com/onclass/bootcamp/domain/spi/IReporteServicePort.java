package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.model.Bootcamp;

public interface IReporteServicePort {
    void enviarReporte(Bootcamp bootcamp);
}
