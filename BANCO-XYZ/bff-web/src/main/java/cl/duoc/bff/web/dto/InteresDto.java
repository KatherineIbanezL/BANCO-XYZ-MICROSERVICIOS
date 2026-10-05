package cl.duoc.bff.web.dto;

public record InteresDto(
    Long cuentaId,
    String nombre,
    Double saldo,
    Integer edad,
    String tipo
) {}