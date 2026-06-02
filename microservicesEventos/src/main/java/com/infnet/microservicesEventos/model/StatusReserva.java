package com.infnet.microservicesEventos.model;

public enum StatusReserva {
    /** Lugares segurados, aguardando confirmação do pagamento. */
    PENDENTE,
    /** Pagamento aprovado: lugares efetivamente vendidos. */
    CONFIRMADA,
    /** Pagamento recusado/erro: lugares devolvidos ao evento. */
    CANCELADA
}
