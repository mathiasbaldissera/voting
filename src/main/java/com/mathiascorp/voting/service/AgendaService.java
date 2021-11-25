package com.mathiascorp.voting.service;


import com.mathiascorp.voting.entity.Agenda;
import com.mathiascorp.voting.entity.Vote;
import com.mathiascorp.voting.enums.AgendaResult;
import com.mathiascorp.voting.enums.AgendaState;
import com.mathiascorp.voting.exception.ApiRuntimeException;
import com.mathiascorp.voting.repository.AgendaRepository;
import java.util.Date;
import javax.transaction.Transactional;
import javax.validation.Valid;
import lombok.val;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class AgendaService {

  private final AgendaRepository repository;
  private final ThreadPoolTaskScheduler scheduler;
  private final VoteService voteService;

  public AgendaService(AgendaRepository repository,
                       @Lazy ThreadPoolTaskScheduler scheduler,
                       @Lazy VoteService voteService) {
    this.repository = repository;
    this.scheduler = scheduler;
    this.voteService = voteService;
  }

  /**
   * Return an Page of Agendas
   *
   * @param pageable page parameters
   * @return page of agendas
   */
  public Page<Agenda> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  /**
   * Returns an Agenda based on a given id
   *
   * @param id the agenda's id
   * @return the agenda
   */
  public Agenda findById(Long id) {
    return repository.findById(id).orElseThrow(() -> new ApiRuntimeException("Agenda not found", HttpStatus.NOT_FOUND));
  }

  /**
   * Saves an Agenda
   *
   * @param agenda the agenda's data to save
   * @return the saved agenda
   */
  public Agenda save(@Valid Agenda agenda) {
    agenda.setId(null);
    return repository.save(agenda);
  }

  /**
   * Deletes an agenda that isn't initiated or finished
   *
   * @param id the agenda's id
   */
  public void delete(Long id) {
    val agenda = findById(id);
    if (!isAgendaPending(agenda)) {
      throw new ApiRuntimeException("Agenda already initiated or finished, unable to delete",
          HttpStatus.PRECONDITION_FAILED);
    }
    repository.delete(agenda);
  }

  /**
   * Starts an Agenda, setting it's state to INITIATED.
   * After *seconds*, the agenda is marked as FINISHED and the result are calculated
   *
   * @param id      the agenda's id
   * @param seconds seconds before the agenda is marked as FINISHED
   */
  @Transactional
  public void start(Long id, Long seconds) {
    val agenda = findById(id);
    if (!isAgendaPending(agenda)) {
      throw new ApiRuntimeException("Agenda already initiated or finished, unable to init",
          HttpStatus.PRECONDITION_FAILED);
    }
    agenda.setState(AgendaState.INITIATED);
    repository.save(agenda);
    scheduler.schedule(
        () -> finishAgenda(agenda),
        getAgendaDeadline(seconds)
    );
  }

  private Date getAgendaDeadline(Long seconds) {
    return new Date(System.currentTimeMillis() + (seconds * 1000));
  }

  /**
   * Finish the agenda and calculate the results
   * Due to the documentation, it's necessary that the method ends in x seconds or minutes, then, the @Schedule is not recommended to use.
   * TODO: make work via kafka, or change the behavior (need to talk with the PO) of the agenda deadline (change *  from 'ends in 120 seconds' to 'ends today at 10:30')
   *
   * @param agenda the agenda to be finished
   */
  @Transactional
  public void finishAgenda(Agenda agenda) {
    agenda.setState(AgendaState.FINISHED);
    calculateResults(agenda);
    repository.save(agenda);
  }

  /**
   * Calculate the results of the agenda based on the majority vote.
   * If the agenda has no vote, it's reproved.
   *
   * @param agenda the agenda to calculate the result
   */
  private void calculateResults(Agenda agenda) {
    refetchVotes(agenda);
    if (agenda.getVotes().size() == 0) {
      agenda.setResult(AgendaResult.REJECTED);
      return;
    }
    val votesNeededToApprove = (double) agenda.getVotes().size() / 2;
    val totalApproves = getApprovesCount(agenda);
    setAgendaResult(agenda, votesNeededToApprove, totalApproves);
  }

  /**
   * Due to async problems with session, after the spring main thread returns, the proxy is closed.
   * This method refetch the agenda's votes, to use in vote count.
   * TODO: make it count via kafka, or change the behavior (need to talk with the PO) of the agenda deadline (change *  from 'ends in 120 seconds' to 'ends today at 10:30')
   *
   * @param agenda
   */
  private void refetchVotes(Agenda agenda) {
    val votes = voteService.findAllByAgenda(agenda);
    agenda.setVotes(votes);
  }

  private void setAgendaResult(Agenda agenda, double votesNeededToApprove, double totalApproves) {
    if (totalApproves < votesNeededToApprove) {
      agenda.setResult(AgendaResult.REJECTED);
    } else if (totalApproves > votesNeededToApprove) {
      agenda.setResult(AgendaResult.APPROVED);
    } else {
      agenda.setResult(AgendaResult.TIE);
    }
  }

  private double getApprovesCount(Agenda agenda) {
    return (double) agenda.getVotes().stream().filter(Vote::isAnApprove).count();
  }

  private boolean isAgendaPending(Agenda agenda) {
    return agenda.getState().equals(AgendaState.PENDING);
  }
}
