package cl.duoc.bff.atm.dto;

public record CuentaCoreDto(
    Long id,
    Long cuentaId,
    String tipo,
    Double monto,
    String fecha
) {}