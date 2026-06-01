package com.onclass.bootcamp.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CapacidadResponse {
    private Long id;
    private String nombre;
}
