package com.mathiascorp.voting.feign;

import com.mathiascorp.voting.dto.AbleToVoteStatusHolderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "legal-id-validator", url ="https://user-info.herokuapp.com")
public interface LegalIdValidator {

  @GetMapping("/users/{legalId}")
  AbleToVoteStatusHolderDTO validateLegalId(@PathVariable String legalId);
}
