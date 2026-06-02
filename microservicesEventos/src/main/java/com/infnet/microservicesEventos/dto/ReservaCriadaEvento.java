package com.infnet.microservicesEventos.dto;

/**
 * Contrato do evento publicado no tópico {@code cinema.reserva.criada} pelo
 * serviço de reservas. O serviço de Eventos usa esses dados para segurar
 * (reservar) os lugares do evento correspondente.
 *
 * Campos esperados:
 *  - idReserva: identificador único da reserva (usado para idempotência);
 *  - idEvento:  id do evento/sessão deste serviço;
 *  - quantidadeLugares: quantos lugares devem ser segurados;
 *  - idUsuario / emailUsuario: dados do cliente (para rastreio/notificação).
 */
public record ReservaCriadaEvento(
        String idReserva,
        Long idEvento,
        int quantidadeLugares,
        String idUsuario,
        String emailUsuario
) {}
