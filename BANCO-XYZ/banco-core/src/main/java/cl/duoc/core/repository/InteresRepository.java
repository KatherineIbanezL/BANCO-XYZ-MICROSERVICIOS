package cl.duoc.core.repository;

import cl.duoc.core.model.Interes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteresRepository extends JpaRepository<Interes, Long> {
}