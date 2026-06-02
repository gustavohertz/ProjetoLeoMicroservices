package com.infnet.microservicesEventos.dto;

import com.infnet.microservicesEventos.model.Evento;
import com.infnet.microservicesEventos.model.StatusEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoResponseDTO(
        Long id,
        String titulo,
        String descricao,
        String sala,
        LocalDateTime dataHoraSessao,
        int capacidadeTotal,
        int lugaresDisponiveis,
        BigDecimal preco,
        StatusEvento status
) {
    public static EventoResponseDTO fromEntity(Evento e) {
        return new EventoResponseDTO(
                e.getId(),
                e.getTitulo(),
                e.getDescricao(),
                e.getSala(),
                e.getDataHoraSessao(),
                e.getCapacidadeTotal(),
                e.getLugaresDisponiveis(),
                e.getPreco(),
                e.getStatus()
        );
    }
}
