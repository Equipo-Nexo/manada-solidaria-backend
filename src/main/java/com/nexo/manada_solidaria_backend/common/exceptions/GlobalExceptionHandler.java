package com.nexo.manada_solidaria_backend.common.exceptions;

import com.nexo.manada_solidaria_backend.common.exceptions.responses.ApiError;
import tools.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Arrays;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> handleNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String mensaje = "El cuerpo de la petición es inválido o está mal formado.";

        InvalidFormatException ife = findInvalidFormat(ex.getCause());
        if (ife != null
                && ife.getTargetType() != null
                && ife.getTargetType().isEnum()) {

            String campo = ife.getPath().stream()
                    .map(ref -> ref.getPropertyName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

            mensaje = "El valor '" + ife.getValue() + "' no es válido para el campo '" + campo
                    + "'. Los valores permitidos son: "
                    + Arrays.toString(ife.getTargetType().getEnumConstants());
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

    private static InvalidFormatException findInvalidFormat(Throwable cause) {
        while (cause != null && !(cause instanceof InvalidFormatException)) {
            cause = cause.getCause();
        }
        return (InvalidFormatException) cause;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiError> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
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
    
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .errors(List.of("Ocurrió un error inesperado en el servidor."))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
                );
    }
}
