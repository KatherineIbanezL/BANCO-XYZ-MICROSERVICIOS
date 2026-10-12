package cl.duoc.clientes.controller;

import cl.duoc.clientes.model.Cliente;
import cl.duoc.clientes.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Cliente> obtenerClientePorRut(@PathVariable String rut) {
        Optional<Cliente> cliente = clienteRepository.findByRut(rut);
        return cliente.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteRepository.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (Exception e) {
            // Manejo básico de errores (ej. Rut duplicado)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteActualizado) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    clienteExistente.setNombre(clienteActualizado.getNombre());
                    clienteExistente.setEmail(clienteActualizado.getEmail());
                    clienteExistente.setTelefono(clienteActualizado.getTelefono());
                    clienteExistente.setDireccion(clienteActualizado.getDireccion());
                    Cliente guardado = clienteRepository.save(clienteExistente);
                    return ResponseEntity.ok(guardado);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}