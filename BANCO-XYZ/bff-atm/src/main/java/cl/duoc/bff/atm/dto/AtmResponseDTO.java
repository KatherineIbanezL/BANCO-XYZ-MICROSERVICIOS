package cl.duoc.bff.atm.dto;

public class AtmResponseDTO {
    private String numeroCuenta;
    private Double saldoActual;
    private String estadoOperacion;

    public AtmResponseDTO(String numeroCuenta, Double saldoActual, String estadoOperacion) {
        this.numeroCuenta = numeroCuenta;
        this.saldoActual = saldoActual;
        this.estadoOperacion = estadoOperacion;
    }

    public String getNumeroCuenta() { return numeroCuenta; }
    public Double getSaldoActual() { return saldoActual; }
    public String getEstadoOperacion() { return estadoOperacion; }
}