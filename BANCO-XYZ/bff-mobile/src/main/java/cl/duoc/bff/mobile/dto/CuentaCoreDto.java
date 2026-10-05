package cl.duoc.bff.mobile.dto;

public record CuentaCoreDto(
    Long id,
    Long cuentaId,
    String tipo,
    Double monto,
    String fecha
) {}