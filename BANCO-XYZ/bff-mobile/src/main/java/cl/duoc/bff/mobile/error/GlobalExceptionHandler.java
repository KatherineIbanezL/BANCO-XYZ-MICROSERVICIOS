package cl.duoc.bff.mobile.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestClientException.class)
    ResponseEntity<Map<String, Object>> serviceUnavailable(RestClientException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 502,
                "error", "BAD_GATEWAY",
                "message", "El BFF Mobile no pudo comunicarse con los microservicios del backend"
        ));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> unexpected(Exception ex) {
        ex.printStackTrace(); 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 500,
                "error", "INTERNAL_ERROR",
                "message", "No fue posible procesar la solicitud"
        ));
    }
}