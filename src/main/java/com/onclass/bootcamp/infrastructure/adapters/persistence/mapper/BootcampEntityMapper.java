package com.onclass.bootcamp.infrastructure.adapters.persistence.mapper;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BootcampEntityMapper {

    @Mapping(target = "capacidades", ignore = true)
    Bootcamp toDomain(BootcampEntity entity);

    @Mapping(target = "id", ignore = true)
    BootcampEntity toEntity(Bootcamp bootcamp);
}
