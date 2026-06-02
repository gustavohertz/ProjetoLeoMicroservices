package com.infnet.microservicesEventos;

/**
 * Tópicos compartilhados do domínio do cinema.
 * Mantém o mesmo contrato (nomes) usado pelos demais microsserviços.
 */
public interface TopicosKafka {
    String RESERVA_CRIADA = "cinema.reserva.criada";
    String PAGAMENTO_APROVADO = "cinema.pagamento.aprovado";
    String PAGAMENTO_RECUSADO = "cinema.pagamento.recusado";
}
