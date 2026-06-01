package com.onclass.bootcamp.domain.excepcion;

import lombok.Getter;

@Getter
public class BootcampException extends RuntimeException {
    private final String code;
    private final String message;

    public BootcampException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
