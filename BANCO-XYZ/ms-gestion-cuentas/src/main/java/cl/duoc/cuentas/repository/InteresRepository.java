package cl.duoc.cuentas.repository;

import cl.duoc.cuentas.model.Interes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InteresRepository extends JpaRepository<Interes, Long> {
    List<Interes> findByCuentaId(Long cuentaId);
}