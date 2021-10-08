package com.turkraft.springfilter.exception;

public class BadFilterSyntaxException extends SpringFilterException {

  private static final long serialVersionUID = 1L;

  public BadFilterSyntaxException(String message, Throwable cause) {
    super(message, cause);
  }

  public BadFilterSyntaxException(String message) {
    super(message);
  }

}
