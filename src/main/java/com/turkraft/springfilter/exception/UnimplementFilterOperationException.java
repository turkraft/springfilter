package com.turkraft.springfilter.exception;

import com.turkraft.springfilter.parser.operation.IOperation;

public class UnimplementFilterOperationException extends SpringFilterException {

  private static final long serialVersionUID = 1L;

  public <E extends Enum<E> & IOperation> UnimplementFilterOperationException(E operation) {
    super("The operation " + operation + " in unimplemented/unsupported");
  }

  public UnimplementFilterOperationException(String message) {
    super(message);
  }

}
