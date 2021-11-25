package com.mathiascorp.voting.repository;

import com.mathiascorp.voting.entity.Agenda;
import com.mathiascorp.voting.entity.Vote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
  boolean existsByAgendaAndLegalId(Agenda agenda, String legalId);

  List<Vote> findAllByAgenda(Agenda agenda);
}
