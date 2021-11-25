package com.mathiascorp.voting.dto;

import com.mathiascorp.voting.enums.VoteChoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteDTO {

  Long agendaId;

  String legalId;

  VoteChoice choice;

}
