package cl.duoc.core.repository;

import cl.duoc.core.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    // Permite buscar registros de la cuenta anual por su ID numérico de cuenta
    List<Cuenta> findByCuentaId(Long cuentaId);
    
    // Permite obtener los últimos movimientos para la vista móvil u operaciones del cajero
    List<Cuenta> findTop5ByCuentaIdOrderByIdDesc(Long cuentaId);
}