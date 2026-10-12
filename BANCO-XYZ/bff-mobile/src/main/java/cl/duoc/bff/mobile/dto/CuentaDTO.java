package cl.duoc.bff.mobile.dto;

public class CuentaDTO {
    private Long id;
    private String numeroCuenta;
    private Double monto; 
    
    public CuentaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
}