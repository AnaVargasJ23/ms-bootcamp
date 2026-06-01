package com.onclass.bootcamp.infrastructure.entrypoints.mapper;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.Capacidad;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampRequest;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampResponse;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.CapacidadIdRequest;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.CapacidadResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BootcampMapper {

    @Mapping(target = "id", ignore = true)
    Bootcamp toDomain(BootcampRequest request);

    Capacidad toDomain(CapacidadIdRequest request);

    BootcampResponse toResponse(Bootcamp bootcamp);

    CapacidadResponse toResponse(Capacidad capacidad);
}
