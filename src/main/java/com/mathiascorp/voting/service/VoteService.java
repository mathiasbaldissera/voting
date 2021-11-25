package com.mathiascorp.voting.service;

import com.mathiascorp.voting.dto.AbleToVoteStatusHolderDTO;
import com.mathiascorp.voting.dto.VoteDTO;
import com.mathiascorp.voting.entity.Agenda;
import com.mathiascorp.voting.entity.Vote;
import com.mathiascorp.voting.enums.AgendaState;
import com.mathiascorp.voting.exception.ApiRuntimeException;
import com.mathiascorp.voting.feign.LegalIdValidator;
import com.mathiascorp.voting.repository.VoteRepository;
import feign.FeignException;
import java.util.List;
import lombok.val;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class VoteService {


  private final VoteRepository repository;
  private final LegalIdValidator validator;
  private final AgendaService agendaService;

  public VoteService(VoteRepository repository, LegalIdValidator validator,
                     AgendaService agendaService) {
    this.repository = repository;
    this.validator = validator;
    this.agendaService = agendaService;
  }

  /**
   * Save an vote for an agenda.
   *
   * @param dto the necessary data for vote
   * @return the vote
   */
  public Vote vote(VoteDTO dto) {
    verifyUserIsAbleToVote(dto);
    val agenda = agendaService.findById(dto.getAgendaId());
    verifyAgendaIfAgendaStateIsInitiated(agenda);
    val exists = repository.existsByAgendaAndLegalId(agenda, dto.getLegalId());
    if (exists) {
      throw new ApiRuntimeException("This associated already voted in this agenda", HttpStatus.PRECONDITION_FAILED);
    }
    return repository.save(new Vote(agenda, dto.getLegalId(), dto.getChoice()));
  }

  /**
   * Verify if the legalId is valid and if the user is able to vote
   *
   * @param dto
   */
  private void verifyUserIsAbleToVote(VoteDTO dto) {
    AbleToVoteStatusHolderDTO ableToVoteStatusHolder;
    try {
      ableToVoteStatusHolder = validator.validateLegalId(dto.getLegalId());
    } catch (FeignException e) {
      throw new ApiRuntimeException("Invalid Legal Id", HttpStatus.BAD_REQUEST);
    }
    if (!ableToVoteStatusHolder.isAbleToVote()) {
      throw new ApiRuntimeException("This associated is unable to vote", HttpStatus.PRECONDITION_FAILED);
    }
  }

  private void verifyAgendaIfAgendaStateIsInitiated(Agenda agenda) {
    if (!agenda.getState().equals(AgendaState.INITIATED)) {
      throw new ApiRuntimeException("This agenda is not initiated or is already finished",
          HttpStatus.PRECONDITION_FAILED);
    }
  }

  public List<Vote> findAllByAgenda(Agenda agenda) {
    return repository.findAllByAgenda(agenda);
  }
}
