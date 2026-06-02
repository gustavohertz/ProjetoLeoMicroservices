package com.infnet.microservicesEventos.dto;

import com.infnet.microservicesEventos.model.StatusEvento;

public record DisponibilidadeDTO(
        Long idEvento,
        String titulo,
        int lugaresDisponiveis,
        StatusEvento status
) {}
