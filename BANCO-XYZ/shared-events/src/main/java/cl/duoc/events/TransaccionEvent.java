package cl.duoc.events;

import java.io.Serializable;

public class TransaccionEvent implements Serializable {
    private Long cuentaId;
    private String tipoTransaccion;
    private Double monto;
    private String origenCanal;

    public TransaccionEvent() {}

    public TransaccionEvent(Long cuentaId, String tipoTransaccion, Double monto, String origenCanal) {
        this.cuentaId = cuentaId;
        this.tipoTransaccion = tipoTransaccion;
        this.monto = monto;
        this.origenCanal = origenCanal;
    }

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }

    public String getTipoTransaccion() { return tipoTransaccion; }
    public void setTipoTransaccion(String tipoTransaccion) { this.tipoTransaccion = tipoTransaccion; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getOrigenCanal() { return origenCanal; }
    public void setOrigenCanal(String origenCanal) { this.origenCanal = origenCanal; }
}