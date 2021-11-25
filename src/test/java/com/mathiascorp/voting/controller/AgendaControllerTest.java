package com.mathiascorp.voting.controller;


import com.mathiascorp.voting.dto.AbleToVoteStatusHolderDTO;
import com.mathiascorp.voting.dto.VoteDTO;
import com.mathiascorp.voting.entity.Agenda;
import com.mathiascorp.voting.enums.AbleToVoteStatus;
import com.mathiascorp.voting.enums.AgendaResult;
import com.mathiascorp.voting.enums.AgendaState;
import com.mathiascorp.voting.enums.VoteChoice;
import com.mathiascorp.voting.feign.LegalIdValidator;
import com.mathiascorp.voting.repository.AgendaRepository;
import com.mathiascorp.voting.repository.VoteRepository;
import com.mathiascorp.voting.service.AgendaService;
import com.mathiascorp.voting.service.VoteService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AgendaControllerTest {
  @Autowired AgendaRepository repository;
  @Autowired AgendaService service;
  @Autowired AgendaController controller;
  @Autowired VoteRepository voteRepository;
  @Mock LegalIdValidator validator;
  VoteService voteService;

  @BeforeEach
  public void init(){
    voteService = new VoteService(voteRepository, validator, service);
  }

  @Test
  @Order(1)
  public void givenAgenda_testSave(){
    Agenda agenda = mockAgenda();
    controller.save(agenda);
    assertEquals(1L, agenda.getId());
  }

  @Test
  @Order(2)
  public void givenAgenda_testStart_shouldInit_shouldFinishAfter3Seconds_shouldBeReproved() throws InterruptedException {
    Agenda agenda = mockAgenda();
    repository.save(agenda);

    controller.start(agenda.getId(), 2L);
    assertEquals(AgendaState.INITIATED, controller.findById(agenda.getId()).getState());
    Thread.sleep(3000L);
    assertEquals(AgendaState.FINISHED, controller.findById(agenda.getId()).getState());
    assertEquals(AgendaState.FINISHED, controller.findById(agenda.getId()).getState());
    assertEquals(AgendaResult.REJECTED, controller.findById(agenda.getId()).getResult());

  }

  @Test
  @Order(3)
  public void givenAgenda_testStart_shouldInit_insertVotes_shouldFinishAfter3Seconds_shouldBeTie() throws InterruptedException {
    Agenda agenda = mockAgenda();
    repository.save(agenda);
    mockValidatorResult();
    controller.start(agenda.getId(), 2L);

    voteService.vote(new VoteDTO(agenda.getId(), "41557616027", VoteChoice.YES));
    voteService.vote(new VoteDTO(agenda.getId(), "11742102069", VoteChoice.YES));
    voteService.vote(new VoteDTO(agenda.getId(), "63707543072", VoteChoice.NO));
    voteService.vote(new VoteDTO(agenda.getId(), "36079642034", VoteChoice.NO));

    Thread.sleep(3000L);

    assertEquals(AgendaState.FINISHED, controller.findById(agenda.getId()).getState());
    assertEquals(AgendaResult.TIE, controller.findById(agenda.getId()).getResult());

  }

  private Agenda mockAgenda() {
    val agenda = new Agenda();
    agenda.setDescription("description");
    return agenda;
  }

  private void mockValidatorResult() {
    when(validator.validateLegalId(any())).thenReturn(new AbleToVoteStatusHolderDTO(AbleToVoteStatus.ABLE_TO_VOTE));
  }
}
