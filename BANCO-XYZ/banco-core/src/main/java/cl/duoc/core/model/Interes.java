package cl.duoc.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "intereses")
public class Interes {

    @Id
    @Column(name = "cuenta_id")
    private Long cuentaId;

    private String nombre;
    private Double saldo;
    private Double edad;
    private String tipo;

    public Interes() {}

    // Getters y Setters
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }

    public Double getEdad() { return edad; }
    public void setEdad(Double edad) { this.edad = edad; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}