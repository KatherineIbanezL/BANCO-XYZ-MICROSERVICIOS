package cl.duoc.bff.atm.dto;

public class AtmResponseDTO {
    private String mensaje;
    private double montoProcesado;

    public AtmResponseDTO() {}

    public AtmResponseDTO(String mensaje, double montoProcesado) {
        this.mensaje = mensaje;
        this.montoProcesado = montoProcesado;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public double getMontoProcesado() { return montoProcesado; }
    public void setMontoProcesado(double montoProcesado) { this.montoProcesado = montoProcesado; }
}