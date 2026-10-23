package com.Biblioteca.BibliotecaUniversitaria.backend.base.utils;

import com.Biblioteca.BibliotecaUniversitaria.backend.base.configs.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseDTOService {
    public ResponseEntity<?> response(Object data, HttpStatus status) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(
                status.getReasonPhrase(), data, status.value());
        return ResponseEntity.status(status).body(responseDTO);
    }
    public ResponseEntity<?>  response(HttpStatus status){
        ResponseDTO<?>  responseDTO = new ResponseDTO<>(
                status.value(), status.getReasonPhrase());
        return new ResponseEntity<>(responseDTO, status);

    }
}
