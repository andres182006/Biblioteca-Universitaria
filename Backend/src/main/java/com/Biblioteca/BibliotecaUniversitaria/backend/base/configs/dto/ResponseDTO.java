package com.Biblioteca.BibliotecaUniversitaria.backend.base.configs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private String message;
    private T data;
    private int status;
    public ResponseDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
