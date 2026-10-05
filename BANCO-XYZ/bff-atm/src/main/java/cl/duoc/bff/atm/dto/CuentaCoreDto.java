package cl.duoc.bff.atm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CuentaCoreDto(
    Long id,
    Long cuentaId,
    @JsonProperty("transaccion") String tipo,
    Double monto,
    String fecha
) {}