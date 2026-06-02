package com.infnet.microservicesEventos.service;

import com.infnet.microservicesEventos.dto.DisponibilidadeDTO;
import com.infnet.microservicesEventos.dto.EventoRequestDTO;
import com.infnet.microservicesEventos.dto.EventoResponseDTO;
import com.infnet.microservicesEventos.dto.PagamentoEvento;
import com.infnet.microservicesEventos.dto.ReservaCriadaEvento;
import com.infnet.microservicesEventos.exception.EventoNotFoundException;
import com.infnet.microservicesEventos.exception.LugaresInsuficientesException;
import com.infnet.microservicesEventos.model.Evento;
import com.infnet.microservicesEventos.model.Reserva;
import com.infnet.microservicesEventos.model.StatusReserva;
import com.infnet.microservicesEventos.repository.EventoRepository;
import com.infnet.microservicesEventos.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoService {

    private static final Logger log = LoggerFactory.getLogger(EventoService.class);

    private final EventoRepository eventoRepository;
    private final ReservaRepository reservaRepository;

    public EventoService(EventoRepository eventoRepository, ReservaRepository reservaRepository) {
        this.eventoRepository = eventoRepository;
        this.reservaRepository = reservaRepository;
    }

    // ------------------------------------------------------------------
    // CRUD do catálogo
    // ------------------------------------------------------------------

    @Transactional
    public EventoResponseDTO criar(EventoRequestDTO dto) {
        Evento evento = new Evento(
                dto.titulo(), dto.descricao(), dto.sala(),
                dto.dataHoraSessao(), dto.capacidadeTotal(), dto.preco());
        return EventoResponseDTO.fromEntity(eventoRepository.save(evento));
    }

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listar() {
        return eventoRepository.findAll().stream()
                .map(EventoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoResponseDTO buscarPorId(Long id) {
        return EventoResponseDTO.fromEntity(obterEvento(id));
    }

    @Transactional(readOnly = true)
    public DisponibilidadeDTO consultarDisponibilidade(Long id) {
        Evento e = obterEvento(id);
        return new DisponibilidadeDTO(e.getId(), e.getTitulo(), e.getLugaresDisponiveis(), e.getStatus());
    }

    /** Atualiza os dados descritivos do evento (não altera capacidade nem lugares já vendidos). */
    @Transactional
    public EventoResponseDTO atualizar(Long id, EventoRequestDTO dto) {
        Evento e = obterEvento(id);
        e.setTitulo(dto.titulo());
        e.setDescricao(dto.descricao());
        e.setSala(dto.sala());
        e.setDataHoraSessao(dto.dataHoraSessao());
        e.setPreco(dto.preco());
        return EventoResponseDTO.fromEntity(eventoRepository.save(e));
    }

    @Transactional
    public void deletar(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new EventoNotFoundException(id);
        }
        eventoRepository.deleteById(id);
    }

    // ------------------------------------------------------------------
    // Reações aos eventos Kafka
    // ------------------------------------------------------------------

    /**
     * Reserva criada: segura os lugares do evento e registra um "hold" PENDENTE.
     * Idempotente por idReserva.
     */
    @Transactional
    public void processarReservaCriada(ReservaCriadaEvento evento) {
        if (reservaRepository.existsByIdReserva(evento.idReserva())) {
            log.info("Reserva {} já processada — ignorando (idempotência).", evento.idReserva());
            return;
        }

        Evento eventoCinema = obterEvento(evento.idEvento());
        try {
            eventoCinema.reservarLugares(evento.quantidadeLugares());
        } catch (IllegalStateException ex) {
            throw new LugaresInsuficientesException(ex.getMessage());
        }
        eventoRepository.save(eventoCinema);

        Reserva reserva = new Reserva(
                evento.idReserva(), eventoCinema, evento.quantidadeLugares(),
                evento.idUsuario(), evento.emailUsuario());
        reservaRepository.save(reserva);

        log.info("Lugares segurados: {} para o evento {} (reserva {}). Restam {} lugares.",
                evento.quantidadeLugares(), eventoCinema.getId(), evento.idReserva(),
                eventoCinema.getLugaresDisponiveis());
    }

    /** Pagamento aprovado: confirma o hold (lugares passam a vendidos). */
    @Transactional
    public void confirmarReserva(PagamentoEvento evento) {
        Reserva reserva = reservaRepository.findByIdReserva(evento.idReserva()).orElse(null);
        if (reserva == null) {
            log.warn("Pagamento APROVADO para reserva desconhecida: {}", evento.idReserva());
            return;
        }
        if (reserva.getStatus() != StatusReserva.PENDENTE) {
            log.info("Reserva {} não está PENDENTE (estado atual: {}) — ignorando confirmação.",
                    evento.idReserva(), reserva.getStatus());
            return;
        }
        reserva.setStatus(StatusReserva.CONFIRMADA);
        reservaRepository.save(reserva);
        log.info("Reserva {} CONFIRMADA. Lugares vendidos no evento {}.",
                evento.idReserva(), reserva.getEvento().getId());
    }

    /** Pagamento recusado: cancela o hold e devolve os lugares ao evento. */
    @Transactional
    public void cancelarReserva(PagamentoEvento evento) {
        Reserva reserva = reservaRepository.findByIdReserva(evento.idReserva()).orElse(null);
        if (reserva == null) {
            log.warn("Pagamento RECUSADO para reserva desconhecida: {}", evento.idReserva());
            return;
        }
        if (reserva.getStatus() != StatusReserva.PENDENTE) {
            log.info("Reserva {} não está PENDENTE (estado atual: {}) — ignorando cancelamento.",
                    evento.idReserva(), reserva.getStatus());
            return;
        }
        Evento eventoCinema = reserva.getEvento();
        eventoCinema.liberarLugares(reserva.getQuantidadeLugares());
        eventoRepository.save(eventoCinema);

        reserva.setStatus(StatusReserva.CANCELADA);
        reservaRepository.save(reserva);

        log.info("Reserva {} CANCELADA. Devolvidos {} lugares ao evento {}. Disponíveis agora: {}.",
                evento.idReserva(), reserva.getQuantidadeLugares(), eventoCinema.getId(),
                eventoCinema.getLugaresDisponiveis());
    }

    private Evento obterEvento(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new EventoNotFoundException(id));
    }
}
