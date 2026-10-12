package cl.duoc.cuentas.dto;


public class LegacyDataDTO {
    // Representa exactamente las columnas de intereses_trimestrales.csv
    private String cuentaId;
    private String nombre;
    private Double saldo;
    private Integer edad;
    private String tipo;

    // ... Getters y Setters
    public String getCuentaId() { return cuentaId; }
    public void setCuentaId(String cuentaId) { this.cuentaId = cuentaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

}