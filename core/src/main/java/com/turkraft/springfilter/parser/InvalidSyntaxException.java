package com.turkraft.springfilter.parser;

import java.io.Serial;

public class InvalidSyntaxException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  public InvalidSyntaxException(String message) {
    super(message);
  }

}
