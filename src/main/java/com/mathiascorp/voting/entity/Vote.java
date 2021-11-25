package com.mathiascorp.voting.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mathiascorp.voting.enums.VoteChoice;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "en_vote")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vote_id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "agenda_id", nullable = false, updatable = false)
  @JsonBackReference
  private Agenda agenda;

  @Column(name = "legal_id", nullable = false, updatable = false)
  private String legalId;


  @Enumerated(EnumType.STRING)
  @Column(name = "choice", nullable = false, updatable = false)
  private VoteChoice choice;

  public Vote(Agenda agenda, String legalId, VoteChoice choice) {
    this.agenda = agenda;
    this.legalId = legalId;
    this.choice = choice;
  }

  public boolean isAnApprove() {
    return this.getChoice().equals(VoteChoice.YES);
  }
}
