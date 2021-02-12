package com.springfilter.compiler.springfilter.token;

import com.springfilter.compiler.compiler.token.IToken;

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
