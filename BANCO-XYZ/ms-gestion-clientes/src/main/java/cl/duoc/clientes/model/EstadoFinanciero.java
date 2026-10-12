package cl.duoc.clientes.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "estados_financieros")
public class EstadoFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId; 

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "tipo_transaccion", nullable = false, length = 50)
    private String tipoTransaccion; 

    @Column(nullable = false)
    private double monto;

    @Column(length = 255)
    private String descripcion;

    public EstadoFinanciero() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getTipoTransaccion() { return tipoTransaccion; }
    public void setTipoTransaccion(String tipoTransaccion) { this.tipoTransaccion = tipoTransaccion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}