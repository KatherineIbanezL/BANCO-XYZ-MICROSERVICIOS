package cl.duoc.pagos.repository;

import cl.duoc.pagos.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    
    // Garantiza que los movimientos más recientes aparezcan arriba en el Dashboard del BFF
    List<Transaccion> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}