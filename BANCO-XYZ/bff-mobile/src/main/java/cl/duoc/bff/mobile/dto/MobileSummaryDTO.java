package cl.duoc.bff.mobile.dto;

import java.util.List;

public class MobileSummaryDTO {
    private String numeroCuenta;
    private Double saldo; // ⚠️ Cambiado a Double
    private List<TransaccionDTO> ultimosMovimientos;

    public MobileSummaryDTO() {}

    public MobileSummaryDTO(String numeroCuenta, Double saldo, List<TransaccionDTO> ultimosMovimientos) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.ultimosMovimientos = ultimosMovimientos;
    }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }

    public List<TransaccionDTO> getUltimosMovimientos() { return ultimosMovimientos; }
    public void setUltimosMovimientos(List<TransaccionDTO> ultimosMovimientos) { this.ultimosMovimientos = ultimosMovimientos; }
}