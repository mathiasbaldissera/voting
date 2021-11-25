# Mathias' voting system

This project is a first version of a voting system.

Some Behaviors:
* You can create an Agenda (from portuguese: pauta)
* You can start an Agenda with the duration of N seconds
* You can vote on an agenda using.
    * An user (identified by the LegalId attribute) can vote once per Agenda.
    * The LegalId is and Brazilian CPF, only digits, validated on an external API
    * If the LegalId is valid, the API will return randomly if the user is able or not to vote.
* After N seconds from the Agenda started, it will be marked as Finished, and the votes will be counted.
* If the majority of votes are YES, the Agenda is approved. 
  If the number of YES and NO are the same, the agenda is marked as TIE.
  If the majority votes NO, or the agenda has 0 votes, it's rejected

OBS: there's a postman collection with the 4 essential methods of the application

## How to build and use.
### Requirements
* Maven
* Docker (with docker-compose)

### Linux
Run the script `buildImage.sh` then the command `docker-composeup`. 
The docker-compose file contains a MySQL database to run with the application, and the application itself.

### Windows
//TODO

## Future modifications
* Change the behavior of "the agenda finishes in N seconds" to "the agenda ends Today at 10:30"
* Publish the results via Kafka messages
