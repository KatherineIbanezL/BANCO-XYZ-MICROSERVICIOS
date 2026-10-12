package cl.duoc.bff.web.dto;

import java.util.List;

public class CuentaDTO {
    private Long id;
    private String numeroCuenta;
    private double monto;
    private String tipoCuenta;
    
    // Nueva propiedad para alojar el historial orquestado
    private List<TransaccionDTO> transacciones; 

    public CuentaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public List<TransaccionDTO> getTransacciones() { return transacciones; }
    public void setTransacciones(List<TransaccionDTO> transacciones) { this.transacciones = transacciones; }
}