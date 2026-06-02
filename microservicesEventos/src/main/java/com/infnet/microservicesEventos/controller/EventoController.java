package com.infnet.microservicesEventos.controller;

import com.infnet.microservicesEventos.dto.DisponibilidadeDTO;
import com.infnet.microservicesEventos.dto.EventoRequestDTO;
import com.infnet.microservicesEventos.dto.EventoResponseDTO;
import com.infnet.microservicesEventos.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(@Valid @RequestBody EventoRequestDTO request,
                                                   UriComponentsBuilder uriBuilder) {
        EventoResponseDTO criado = eventoService.criar(request);
        URI uri = uriBuilder.path("/eventos/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar() {
        return ResponseEntity.ok(eventoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<DisponibilidadeDTO> disponibilidade(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.consultarDisponibilidade(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody EventoRequestDTO request) {
        return ResponseEntity.ok(eventoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        eventoService.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
