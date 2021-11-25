CREATE TABLE en_agenda (
  agenda_id int(11) NOT NULL AUTO_INCREMENT,
  description TEXT NOT NULL,
  state VARCHAR(10) NOT NULL,
  result VARCHAR(10),
  PRIMARY KEY (agenda_id)
);
