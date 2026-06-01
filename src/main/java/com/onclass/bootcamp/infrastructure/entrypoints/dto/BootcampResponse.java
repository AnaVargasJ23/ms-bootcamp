package com.onclass.bootcamp.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BootcampResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaLanzamiento;
    private Integer duracion;
    private List<CapacidadResponse> capacidades;
}
