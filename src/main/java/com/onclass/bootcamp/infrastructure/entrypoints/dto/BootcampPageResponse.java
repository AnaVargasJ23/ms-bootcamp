package com.onclass.bootcamp.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BootcampPageResponse {
    private List<BootcampResponse> bootcamps;
    private int paginaActual;
    private int totalPaginas;
    private long totalElementos;
}
