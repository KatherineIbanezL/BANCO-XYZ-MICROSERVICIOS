package cl.duoc.bff.mobile.dto;

public record InteresDto(
    Long cuentaId,
    String nombre,
    Double saldo,
    Double edad,
    String tipo
) {}