package com.turkraft.springfilter.token;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Parenthesis implements IToken {

  private Type type;

  public enum Type {
    OPEN, CLOSE
  }

}
