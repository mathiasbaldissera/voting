package com.mathiascorp.voting.dto;

import com.mathiascorp.voting.enums.AbleToVoteStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbleToVoteStatusHolderDTO {

  private AbleToVoteStatus status;

  public boolean isAbleToVote(){
    return this.getStatus().equals(AbleToVoteStatus.ABLE_TO_VOTE);
  }

}
