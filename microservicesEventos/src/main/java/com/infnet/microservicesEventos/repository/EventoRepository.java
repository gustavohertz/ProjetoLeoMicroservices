package com.infnet.microservicesEventos.repository;

import com.infnet.microservicesEventos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}
