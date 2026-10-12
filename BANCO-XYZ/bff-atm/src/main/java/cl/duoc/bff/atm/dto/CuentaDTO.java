package cl.duoc.bff.atm.dto;

public class CuentaDTO {
    private Long id;
    private String numeroCuenta;
    private double monto;

    public CuentaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
}