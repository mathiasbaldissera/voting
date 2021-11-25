CREATE TABLE en_vote (
  vote_id int(11) NOT NULL AUTO_INCREMENT,
  agenda_id int(11) NOT NULL,
  legal_id VARCHAR(20) NOT NULL,
  choice VARCHAR(5) NOT NULL,
  PRIMARY KEY (vote_id),
  UNIQUE KEY uk_agenda_legal_id_combination (agenda_id, legal_id)
);
