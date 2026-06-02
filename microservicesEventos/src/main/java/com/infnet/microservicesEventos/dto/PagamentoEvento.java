package com.infnet.microservicesEventos.dto;

/**
 * Contrato dos eventos de pagamento (tópicos {@code cinema.pagamento.aprovado}
 * e {@code cinema.pagamento.recusado}). Espelha o record publicado pelo
 * serviço de pagamento; o que importa aqui é o {@code idReserva}, que liga o
 * pagamento ao "hold" de lugares feito anteriormente.
 */
public record PagamentoEvento(
        String idReserva,
        String idUsuario,
        String emailUsuario,
        String cpfUser,
        String status,
        String mensagemErro,
        String idPagamento
) {}
