package cl.duoc.cuentas.consumer;

import cl.duoc.events.TransaccionEvent;
import cl.duoc.cuentas.service.CuentaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PagoConsumer {

    private static final Logger log = LoggerFactory.getLogger(PagoConsumer.class);

    private final CuentaService cuentaService;

    public PagoConsumer(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @KafkaListener(topics = "pagos-procesados-topic", groupId = "cuentas-group")
    public void consumirTransaccion(TransaccionEvent evento) {
        try {
            if (evento == null || evento.getCuentaId() == null || evento.getMonto() == null) {
                log.warn("Evento recibido en Kafka está incompleto o es nulo: {}", evento);
                return;
            }

            String tipo = evento.getTipoTransaccion() != null ? evento.getTipoTransaccion().toUpperCase() : "";

            // 1. Evalúa si la operación SUMA saldo (Créditos, Depósitos o Abonos)
            boolean esCredito = tipo.contains("CREDITO") || tipo.contains("DEPOSITO") || tipo.contains("ABONO");

            // 2. Si es crédito suma (+), si es débito/retiro/pago resta (-)
            double montoAbsoluto = Math.abs(evento.getMonto());
            double variacionMonto = esCredito ? montoAbsoluto : -montoAbsoluto;

            // 3. Actualiza el saldo en la BD de ms-gestion-cuentas
            cuentaService.actualizarSaldo(evento.getCuentaId(), variacionMonto);

            log.info("Kafka: Saldo actualizado para cuenta ID: {} (Tipo: {}, Variación: {}). Canal: {}",
                    evento.getCuentaId(), tipo, variacionMonto, evento.getOrigenCanal());

        } catch (Exception e) {
            log.error("Error al procesar evento de Kafka para la cuenta {}: {}", 
                    evento != null ? evento.getCuentaId() : "desconocida", e.getMessage(), e);
        }
    }
}