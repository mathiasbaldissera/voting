FROM openjdk:8-jdk-alpine

EXPOSE 8080

RUN apk add bash

COPY target/*.jar app.jar
COPY wait-for-it.sh wait-for-it.sh

RUN chmod +x wait-for-it.sh

ENTRYPOINT ["java","-jar","app.jar"]