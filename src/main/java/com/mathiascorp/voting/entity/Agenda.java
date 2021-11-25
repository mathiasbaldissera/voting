package com.mathiascorp.voting.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mathiascorp.voting.enums.AgendaResult;
import com.mathiascorp.voting.enums.AgendaState;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that represents an Agenda
 * (from portuguese: pauta. <https://dictionary.cambridge.org/pt/dicionario/portugues-ingles/pauta> )
 */
@Entity
@Table(name = "en_agenda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agenda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "agenda_id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "description")
  private String description;

  @OneToMany(mappedBy = "agenda")
  @JsonManagedReference
  private List<Vote> votes;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false)
  private AgendaState state;

  @Enumerated(EnumType.STRING)
  @Column(name = "result")
  private AgendaResult result;

  @PrePersist
  private void prePersist() {
    state = AgendaState.PENDING;
    votes = null;
    result = null;
  }
}
