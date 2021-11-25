package com.mathiascorp.voting.controller;

import com.mathiascorp.voting.dto.VoteDTO;
import com.mathiascorp.voting.entity.Vote;
import com.mathiascorp.voting.service.VoteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vote")
public class VoteController {
  private final VoteService service;

  public VoteController(VoteService service) {
    this.service = service;
  }

  @PostMapping
  public Vote vote(@RequestBody VoteDTO dto) {
    return service.vote(dto);
  }
}
