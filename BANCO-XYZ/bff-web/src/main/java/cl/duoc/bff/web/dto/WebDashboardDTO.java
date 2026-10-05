package cl.duoc.bff.web.dto;

import java.util.List;

public class WebDashboardDTO {
    private String numeroCuenta;
    private String nombreTitular;
    private Double saldoActual;
    private List<CuentaCoreDto> historialTransacciones; // ¡Usa el DTO del BFF, no la entidad Cuenta del Core!

    public WebDashboardDTO(String numeroCuenta, String nombreTitular, Double saldoActual, List<CuentaCoreDto> historialTransacciones) {
        this.numeroCuenta = numeroCuenta;
        this.nombreTitular = nombreTitular;
        this.saldoActual = saldoActual;
        this.historialTransacciones = historialTransacciones;
    }

    public String getNumeroCuenta() { return numeroCuenta; }
    public String getNombreTitular() { return nombreTitular; }
    public Double getSaldoActual() { return saldoActual; }
    public List<CuentaCoreDto> getHistorialTransacciones() { return historialTransacciones; }
}