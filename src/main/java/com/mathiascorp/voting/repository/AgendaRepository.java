package com.mathiascorp.voting.repository;

import com.mathiascorp.voting.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
