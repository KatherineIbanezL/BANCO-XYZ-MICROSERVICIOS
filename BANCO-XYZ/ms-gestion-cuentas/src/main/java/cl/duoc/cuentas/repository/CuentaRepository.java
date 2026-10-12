package cl.duoc.cuentas.repository;

import cl.duoc.cuentas.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    List<Cuenta> findByClienteId(Long clienteId);
}