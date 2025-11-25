package pe.edu.vallegrande.vg_ms_assistance.infrastructure.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        return Mono.just(ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST)));
    }
    
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(
            WebExchangeBindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        Map<String, Object> response = createErrorResponse(
            "Validation failed", HttpStatus.BAD_REQUEST);
        response.put("errors", errors);
        
        return Mono.just(ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response));
    }
    
    @ExceptionHandler(org.springframework.web.reactive.resource.NoResourceFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNoResourceFoundException(
            org.springframework.web.reactive.resource.NoResourceFoundException ex) {
        // Ignorar errores de favicon y otros recursos estáticos
        if (ex.getMessage().contains("favicon.ico") || ex.getMessage().contains("static resource")) {
            log.debug("Static resource not found (ignored): {}", ex.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        }
        log.warn("Resource not found: {}", ex.getMessage());
        return Mono.just(ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(createErrorResponse("Resource not found", HttpStatus.NOT_FOUND)));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(createErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR)));
    }
    
    private Map<String, Object> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return response;
    }
}
