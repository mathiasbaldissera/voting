package com.mathiascorp.voting.exception;

import org.springframework.http.HttpStatus;

public class ApiRuntimeException extends RuntimeException {

  private static final long serialVersionUID = -3882807453175214074L;

  final HttpStatus status;

  public ApiRuntimeException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
