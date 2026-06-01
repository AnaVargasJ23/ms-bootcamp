package com.onclass.bootcamp.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BootcampErrorEnum {

    NOMBRE_OBLIGATORIO("BOOT-001", "El nombre es obligatorio"),
    NOMBRE_MAX_50("BOOT-002", "El nombre no puede superar 50 caracteres"),
    DESCRIPCION_OBLIGATORIA("BOOT-003", "La descripción es obligatoria"),
    DESCRIPCION_MAX_90("BOOT-004", "La descripción no puede superar 90 caracteres"),
    CAPACIDADES_MIN_1("BOOT-005", "El bootcamp debe tener mínimo 1 capacidad"),
    CAPACIDADES_MAX_4("BOOT-006", "El bootcamp no puede tener más de 4 capacidades"),
    CAPACIDADES_REPETIDAS("BOOT-007", "El bootcamp no puede tener capacidades repetidas"),
    CAPACIDAD_NO_EXISTE("BOOT-008", "Una o más capacidades no existen"),
    NOMBRE_DUPLICADO("BOOT-009", "Ya existe un bootcamp con ese nombre"),
    FECHA_OBLIGATORIA("BOOT-010", "La fecha de lanzamiento es obligatoria"),
    DURACION_OBLIGATORIA("BOOT-011", "La duración es obligatoria");

    private final String code;
    private final String message;
}
