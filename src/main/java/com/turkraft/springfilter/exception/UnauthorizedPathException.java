package com.turkraft.springfilter.exception;

import javax.persistence.criteria.Path;

public class UnauthorizedPathException extends SpringFilterException {

  private static final long serialVersionUID = 1L;

  public UnauthorizedPathException(Path<?> path, String field) {
    super("Unauthorized to access field " + field);
  }

}
