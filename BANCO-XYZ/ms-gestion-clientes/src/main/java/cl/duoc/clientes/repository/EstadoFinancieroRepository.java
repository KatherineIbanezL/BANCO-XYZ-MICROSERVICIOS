package cl.duoc.clientes.repository;

import cl.duoc.clientes.model.EstadoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoFinancieroRepository extends JpaRepository<EstadoFinanciero, Long> {
    List<EstadoFinanciero> findByClienteId(Long clienteId);
}