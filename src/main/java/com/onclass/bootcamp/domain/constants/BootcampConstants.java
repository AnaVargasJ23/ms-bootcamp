package com.onclass.bootcamp.domain.constants;

public class BootcampConstants {
    private BootcampConstants() {}
    public static final int CAPACIDADES_MIN = 1;
    public static final int CAPACIDADES_MAX = 4;
    public static final int NOMBRE_MAX_LENGTH = 50;
    public static final int DESCRIPCION_MAX_LENGTH = 90;
    public static final String CAPACIDAD_BASE_URL = "http://localhost:8081";
    public static final String CAPACIDAD_ENDPOINT = "/api/v1/capacidades/paginado?page=0&size=1&ordenarPor=nombre&direccion=asc";
    public static final String CAPACIDAD_BUSCAR_ENDPOINT = "/api/v1/capacidades/{id}";
}
