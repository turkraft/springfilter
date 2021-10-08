package com.turkraft.springfilter.exception;

public class FilterBuilderException extends SpringFilterException {

  private static final long serialVersionUID = 1L;

  public FilterBuilderException(String message, Throwable cause) {
    super(message, cause);
  }

  public FilterBuilderException(String message) {
    super(message);
  }

}
