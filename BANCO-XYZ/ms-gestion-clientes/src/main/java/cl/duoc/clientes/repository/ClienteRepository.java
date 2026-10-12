package cl.duoc.clientes.repository;

import cl.duoc.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Métodos personalizados útiles para la búsqueda de clientes
    Optional<Cliente> findByRut(String rut);
    Optional<Cliente> findByEmail(String email);
    
}