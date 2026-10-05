package cl.duoc.bff.web.dto;

public record CuentaCoreDto(
    Long id,
    Long cuentaId,
    String tipo,
    Double monto,
    String fecha
) {}