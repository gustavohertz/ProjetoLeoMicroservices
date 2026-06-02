package com.infnet.microservicesEventos.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Dados para criação/atualização de um evento (sessão).
 */
public record EventoRequestDTO(

        @NotBlank(message = "O título é obrigatório")
        String titulo,

        String descricao,

        @NotBlank(message = "A sala é obrigatória")
        String sala,

        @NotNull(message = "A data/hora da sessão é obrigatória")
        @Future(message = "A sessão deve ser no futuro")
        LocalDateTime dataHoraSessao,

        @Min(value = 1, message = "A capacidade deve ser de pelo menos 1 lugar")
        int capacidadeTotal,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = true, message = "O preço não pode ser negativo")
        BigDecimal preco
) {}
