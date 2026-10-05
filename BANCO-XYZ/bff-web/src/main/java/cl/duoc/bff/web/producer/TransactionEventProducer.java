package cl.duoc.bff.web.producer;

import cl.duoc.events.TransaccionEvent; // Import de shared-events
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventProducer {

    private final KafkaTemplate<String, TransaccionEvent> kafkaTemplate;
    private static final String TOPIC = "transacciones-topic";

    public TransactionEventProducer(KafkaTemplate<String, TransaccionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEventoTransaccion(TransaccionEvent evento) {
        this.kafkaTemplate.send(TOPIC, String.valueOf(evento.getCuentaId()), evento);
    }
}