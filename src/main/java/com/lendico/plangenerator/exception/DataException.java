package com.lendico.plangenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DataException extends RuntimeException {
  public DataException() {
    super();
  }

  public DataException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public DataException(final String message) {
    super(message);
  }
}
