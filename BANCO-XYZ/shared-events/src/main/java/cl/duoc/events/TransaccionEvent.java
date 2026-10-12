package cl.duoc.events;

import java.io.Serializable;

public class TransaccionEvent implements Serializable {
    private Long cuentaId;
    private String tipoTransaccion; // "RETIRO", "DEPOSITO"
    private Double monto;
    private String origenCanal;     // "ATM", "MOBILE", "WEB"
    private Long transaccionId;     // ID asignado en la BD de ms-procesamiento-pagos

    public TransaccionEvent() {}

    public TransaccionEvent(Long cuentaId, String tipoTransaccion, Double monto, String origenCanal, Long transaccionId) {
        this.cuentaId = cuentaId;
        this.tipoTransaccion = tipoTransaccion;
        this.monto = monto;
        this.origenCanal = origenCanal;
        this.transaccionId = transaccionId;
    }

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }

    public String getTipoTransaccion() { return tipoTransaccion; }
    public void setTipoTransaccion(String tipoTransaccion) { this.tipoTransaccion = tipoTransaccion; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getOrigenCanal() { return origenCanal; }
    public void setOrigenCanal(String origenCanal) { this.origenCanal = origenCanal; }

    public Long getTransaccionId() { return transaccionId; }
    public void setTransaccionId(Long transaccionId) { this.transaccionId = transaccionId; }
}