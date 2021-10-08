package com.turkraft.springfilter.exception;

import javax.persistence.criteria.Path;

public class UnauthorizedFilterPathException extends SpringFilterException {

  private static final long serialVersionUID = 1L;

  public UnauthorizedFilterPathException(Path<?> path, String field) {
    super("Unauthorized to access field " + field);
  }

}
