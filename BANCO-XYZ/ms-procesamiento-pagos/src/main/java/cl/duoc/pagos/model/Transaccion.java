package cl.duoc.pagos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cuentaId;

    @Column(nullable = false, length = 30)
    private String tipo; // RETIRO, DEPOSITO, DEBITO, CREDITO

    @Column(nullable = false)
    private double monto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 20)
    private String estado; // COMPLETADA, FALLIDA

    public Transaccion() {}

    public Transaccion(Long cuentaId, String tipo, double monto, LocalDateTime fecha, String estado) {
        this.cuentaId = cuentaId;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}