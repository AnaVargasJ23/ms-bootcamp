package com.onclass.bootcamp.infrastructure.adapters.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table("bootcamp_capacidad")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BootcampCapacidadEntity {
    private Long bootcampId;
    private Long capacidadId;
}
