package com.infnet.microservicesEventos.kafka;

import com.infnet.microservicesEventos.TopicosKafka;
import com.infnet.microservicesEventos.dto.PagamentoEvento;
import com.infnet.microservicesEventos.dto.ReservaCriadaEvento;
import com.infnet.microservicesEventos.service.EventoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Ponte entre os tópicos do cinema e o serviço de Eventos:
 *  - reserva.criada      -> segura lugares (hold PENDENTE)
 *  - pagamento.aprovado  -> confirma a reserva
 *  - pagamento.recusado  -> devolve os lugares
 */
@Component
public class EventosConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventosConsumer.class);

    private final EventoService eventoService;

    public EventosConsumer(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @KafkaListener(topics = TopicosKafka.RESERVA_CRIADA, groupId = "eventosGroup")
    public void onReservaCriada(ReservaCriadaEvento evento) {
        log.info("Recebido {} -> {}", TopicosKafka.RESERVA_CRIADA, evento);
        eventoService.processarReservaCriada(evento);
    }

    @KafkaListener(topics = TopicosKafka.PAGAMENTO_APROVADO, groupId = "eventosGroup")
    public void onPagamentoAprovado(PagamentoEvento evento) {
        log.info("Recebido {} -> reserva {}", TopicosKafka.PAGAMENTO_APROVADO, evento.idReserva());
        eventoService.confirmarReserva(evento);
    }

    @KafkaListener(topics = TopicosKafka.PAGAMENTO_RECUSADO, groupId = "eventosGroup")
    public void onPagamentoRecusado(PagamentoEvento evento) {
        log.info("Recebido {} -> reserva {}", TopicosKafka.PAGAMENTO_RECUSADO, evento.idReserva());
        eventoService.cancelarReserva(evento);
    }
}
