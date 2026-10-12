package cl.duoc.pagos.dto;

public class MovimientoDiarioDTO {
    private String id;
    private String fecha;
    private Double monto; 
    private String tipo;

    public MovimientoDiarioDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}