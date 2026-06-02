package com.infnet.microservicesEventos.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa uma sessão/evento do cinema disponível para reserva.
 */
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome do filme / evento. */
    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descricao;

    /** Sala em que a sessão acontece. */
    @Column(nullable = false)
    private String sala;

    @Column(name = "data_hora_sessao", nullable = false)
    private LocalDateTime dataHoraSessao;

    @Column(name = "capacidade_total", nullable = false)
    private int capacidadeTotal;

    @Column(name = "lugares_disponiveis", nullable = false)
    private int lugaresDisponiveis;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status;

    protected Evento() {
        // Construtor exigido pelo JPA
    }

    public Evento(String titulo, String descricao, String sala, LocalDateTime dataHoraSessao,
                  int capacidadeTotal, BigDecimal preco) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.sala = sala;
        this.dataHoraSessao = dataHoraSessao;
        this.capacidadeTotal = capacidadeTotal;
        this.lugaresDisponiveis = capacidadeTotal;
        this.preco = preco;
        this.status = StatusEvento.ATIVO;
    }

    /** Segura (reserva) uma quantidade de lugares, atualizando o status quando esgota. */
    public void reservarLugares(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de lugares deve ser positiva.");
        }
        if (quantidade > this.lugaresDisponiveis) {
            throw new IllegalStateException(
                    "Lugares insuficientes. Disponíveis: " + this.lugaresDisponiveis + ", solicitados: " + quantidade);
        }
        this.lugaresDisponiveis -= quantidade;
        if (this.lugaresDisponiveis == 0) {
            this.status = StatusEvento.ESGOTADO;
        }
    }

    /** Devolve lugares ao evento (ex.: pagamento recusado). */
    public void liberarLugares(int quantidade) {
        if (quantidade <= 0) {
            return;
        }
        this.lugaresDisponiveis = Math.min(this.capacidadeTotal, this.lugaresDisponiveis + quantidade);
        if (this.status == StatusEvento.ESGOTADO && this.lugaresDisponiveis > 0) {
            this.status = StatusEvento.ATIVO;
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public LocalDateTime getDataHoraSessao() {
        return dataHoraSessao;
    }

    public void setDataHoraSessao(LocalDateTime dataHoraSessao) {
        this.dataHoraSessao = dataHoraSessao;
    }

    public int getCapacidadeTotal() {
        return capacidadeTotal;
    }

    public int getLugaresDisponiveis() {
        return lugaresDisponiveis;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public void setStatus(StatusEvento status) {
        this.status = status;
    }
}
