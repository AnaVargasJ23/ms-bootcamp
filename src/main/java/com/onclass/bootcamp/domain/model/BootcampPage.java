package com.onclass.bootcamp.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BootcampPage {
    private List<Bootcamp> bootcamps;
    private int paginaActual;
    private int totalPaginas;
    private long totalElementos;
}
