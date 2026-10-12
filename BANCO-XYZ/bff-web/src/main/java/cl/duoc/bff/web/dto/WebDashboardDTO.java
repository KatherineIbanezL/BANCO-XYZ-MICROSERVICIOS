package cl.duoc.bff.web.dto;

import java.util.List;

public class WebDashboardDTO {
    private ClienteDTO cliente;
    private List<CuentaDTO> cuentas;

    public WebDashboardDTO() {}

    public WebDashboardDTO(ClienteDTO cliente, List<CuentaDTO> cuentas) {
        this.cliente = cliente;
        this.cuentas = cuentas;
    }

    public ClienteDTO getCliente() { return cliente; }
    public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }

    public List<CuentaDTO> getCuentas() { return cuentas; }
    public void setCuentas(List<CuentaDTO> cuentas) { this.cuentas = cuentas; }
}