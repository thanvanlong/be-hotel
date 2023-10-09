package com.tl.hotelproject.controller.excpetion;

import com.tl.hotelproject.entity.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        String message = "";
        for (ObjectError error :
                ex.getBindingResult().getAllErrors()) {
            message =  error.getDefaultMessage();;
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(null, "400", message, false ));
    }

}
