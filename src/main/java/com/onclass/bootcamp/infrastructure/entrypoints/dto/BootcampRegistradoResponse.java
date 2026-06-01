package com.onclass.bootcamp.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BootcampRegistradoResponse {
    private Long id;
    private String nombre;
    private String mensaje;
}