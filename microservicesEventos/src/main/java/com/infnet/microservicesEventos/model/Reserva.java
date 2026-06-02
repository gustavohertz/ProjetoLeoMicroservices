package com.infnet.microservicesEventos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Registro local do "hold" de lugares feito a partir de um evento de reserva.
 * Permite confirmar ou devolver os lugares quando o pagamento é resolvido,
 * além de garantir idempotência por {@code idReserva}.
 */
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador da reserva originado no serviço de reservas (chave de negócio). */
    @Column(name = "id_reserva", nullable = false, unique = true)
    private String idReserva;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(name = "quantidade_lugares", nullable = false)
    private int quantidadeLugares;

    private String idUsuario;

    private String emailUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReserva status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected Reserva() {
        // Construtor exigido pelo JPA
    }

    public Reserva(String idReserva, Evento evento, int quantidadeLugares, String idUsuario, String emailUsuario) {
        this.idReserva = idReserva;
        this.evento = evento;
        this.quantidadeLugares = quantidadeLugares;
        this.idUsuario = idUsuario;
        this.emailUsuario = emailUsuario;
        this.status = StatusReserva.PENDENTE;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public Evento getEvento() {
        return evento;
    }

    public int getQuantidadeLugares() {
        return quantidadeLugares;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
