package cl.duoc.bff.atm.dto;

public record InteresDto(
    Long cuentaId,
    String nombre,
    Double saldo,
    Integer edad,
    String tipo
) {}