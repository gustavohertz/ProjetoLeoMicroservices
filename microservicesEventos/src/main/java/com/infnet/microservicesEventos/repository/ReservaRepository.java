package com.infnet.microservicesEventos.repository;

import com.infnet.microservicesEventos.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findByIdReserva(String idReserva);

    boolean existsByIdReserva(String idReserva);
}
