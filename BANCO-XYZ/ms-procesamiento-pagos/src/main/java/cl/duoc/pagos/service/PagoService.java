package cl.duoc.pagos.service;

import cl.duoc.events.TransaccionEvent;
import cl.duoc.pagos.model.Transaccion;
import cl.duoc.pagos.repository.TransaccionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final TransaccionRepository transaccionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PagoService(TransaccionRepository transaccionRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.transaccionRepository = transaccionRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Transaccion procesarRetiro(Long cuentaId, double monto, String canal) {
        if (monto <= 0) {
            log.warn("Intento de retiro inválido de ${} en la cuenta {} desde canal {}", monto, cuentaId, canal);
            kafkaTemplate.send("alertas-seguridad-topic", "Intento de retiro inválido de $" + monto + " desde canal: " + canal);
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        // 1. Guarda transacción localmente en MySQL
        Transaccion tx = new Transaccion(cuentaId, "RETIRO", monto, LocalDateTime.now(), "COMPLETADA");
        Transaccion guardada = transaccionRepository.save(tx);

        // 2. Publica evento en Kafka hacia ms-gestion-cuentas
        TransaccionEvent evento = new TransaccionEvent(cuentaId, "RETIRO", monto, canal, guardada.getId());
        kafkaTemplate.send("pagos-procesados-topic", evento);

        log.info("Retiro procesado exitosamente ID: {} para la cuenta {}", guardada.getId(), cuentaId);
        return guardada;
    }

    // Retorna los movimientos ordenados del más reciente al más antiguo
    @Transactional(readOnly = true)
    public List<Transaccion> obtenerHistorialPorCuenta(Long cuentaId) {
        return transaccionRepository.findByCuentaIdOrderByFechaDesc(cuentaId);
    }
}