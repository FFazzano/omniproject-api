package com.omniproject.API.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Avisa o Spring que este método vai tratar os erros de Validação (@Valid)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Pega todos os erros que aconteceram e monta um "Dicionário" (Campo -> Mensagem)
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
    // NOVO: Captura qualquer outro erro inesperado do sistema (Erro 500)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleGenericExceptions(Exception ex) {
        // Armadilha 2: OBRIGATÓRIO imprimir no console para não engolir o stacktrace original do erro 500
        ex.printStackTrace(); 
        
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Ocorreu um erro interno no servidor.");
        error.put("detalhe", ex.getMessage()); // Mostra o motivo técnico real
        return error;
    }
}