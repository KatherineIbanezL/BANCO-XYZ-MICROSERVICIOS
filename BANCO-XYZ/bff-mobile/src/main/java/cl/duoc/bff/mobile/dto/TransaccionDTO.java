package cl.duoc.bff.mobile.dto;

import java.time.LocalDateTime;

public class TransaccionDTO {
    private Long id;
    private String tipo;
    private Double monto; 
    private String estado;
    private LocalDateTime fecha;

    public TransaccionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}