package cl.duoc.core.consumer;

import cl.duoc.events.TransaccionEvent; // Import de shared-events
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventConsumer.class);

    @KafkaListener(topics = "transacciones-topic", groupId = "banco-core-group")
    public void procesarEventoTransaccion(TransaccionEvent evento) {
        log.info("Evento recibido desde Kafka [Canal: {}]: Cuenta ID = {}, Tipo = {}, Monto = {}", 
                evento.getOrigenCanal(), evento.getCuentaId(), evento.getTipoTransaccion(), evento.getMonto());
    }
}