package cl.duoc.bff.mobile.dto;

import java.util.List;

public class MobileSummaryDTO {
    private String numeroCuenta;
    private Double saldoActual;
    private List<CuentaCoreDto> ultimasTransacciones; // DTO independiente

    public MobileSummaryDTO(String numeroCuenta, Double saldoActual, List<CuentaCoreDto> ultimasTransacciones) {
        this.numeroCuenta = numeroCuenta;
        this.saldoActual = saldoActual;
        this.ultimasTransacciones = ultimasTransacciones;
    }

    public String getNumeroCuenta() { return numeroCuenta; }
    public Double getSaldoActual() { return saldoActual; }
    public List<CuentaCoreDto> getUltimasTransacciones() { return ultimasTransacciones; }
}