package cl.duoc.cuentas.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "intereses")
public class Interes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cuentaId;

    @Column(nullable = false)
    private double montoInteres;

    @Column(nullable = false)
    private LocalDate fechaAplicacion;

    // Constructores, Getters y Setters
    public Interes() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }

    public double getMontoInteres() { return montoInteres; }
    public void setMontoInteres(double montoInteres) { this.montoInteres = montoInteres; }

    public LocalDate getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(LocalDate fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }
}