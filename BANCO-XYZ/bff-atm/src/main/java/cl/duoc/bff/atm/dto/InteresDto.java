package cl.duoc.bff.atm.dto;

public record InteresDto(
    Long cuentaId,
    String nombre,
    Double saldo,
    Double edad,
    String tipo
) {}