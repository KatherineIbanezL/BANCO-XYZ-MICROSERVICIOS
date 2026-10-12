package cl.duoc.cuentas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroCuenta;

    @Column(nullable = false)
    private Long clienteId; // Relación lógica con ms-gestion-clientes

    @Column(nullable = false)
    private Double monto; // Saldo actual

    @Column(nullable = false)
    private String tipoCuenta;

    // REQUERIDO POR JPA: Constructor por defecto sin argumentos
    public Cuenta() {}

    public Cuenta(String numeroCuenta, Long clienteId, Double monto, String tipoCuenta) {
        this.numeroCuenta = numeroCuenta;
        this.clienteId = clienteId;
        this.monto = monto;
        this.tipoCuenta = tipoCuenta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }
}