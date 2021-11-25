#!/bin/sh
mvn clean package
docker image build --tag mathiasbald/voting .
