package com.mathiascorp.voting.controller;

import com.mathiascorp.voting.entity.Agenda;
import com.mathiascorp.voting.service.AgendaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/agenda")
@RestController
public class AgendaController {
  private final AgendaService service;

  public AgendaController(AgendaService service) {
    this.service = service;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<Agenda> findAll(Pageable pageable) {
    return service.findAll(pageable);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Agenda findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Agenda save(@RequestBody Agenda agenda) {
    return service.save(agenda);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @PatchMapping("/start/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void start(@PathVariable Long id, @RequestParam Long seconds) {
    service.start(id, seconds);
  }
}
