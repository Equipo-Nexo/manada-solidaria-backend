package com.nexo.manada_solidaria_backend.common.exceptions;

import com.nexo.manada_solidaria_backend.common.exceptions.responses.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException responseStatusException, HttpServletRequest request) {
        return ResponseEntity
                .status(responseStatusException.getStatusCode())
                .body(ApiError.builder()
                        .status(responseStatusException.getStatusCode().value())
                        .errors(List.of(responseStatusException.getReason()))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Stream<String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage);

        Stream<String> globalErrores = ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(
                                Stream.concat(errores, globalErrores).toList()
                        )
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
                );
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiError> handleTypeMismatchException(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String mensaje = "El valor '" + ex.getValue() + "' no es válido. ";

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] enumConstants = ex.getRequiredType().getEnumConstants();
            mensaje += "Los tipos permitidos son: " + java.util.Arrays.toString(enumConstants);
        } else {
            mensaje += "El parámetro debe ser del tipo correcto.";
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(List.of(mensaje))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
                );
    }
}
