package com.torshid.springfilter.token;

import com.torshid.compiler.token.Token;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Parenthesis extends Token {

  private Type type;

  public enum Type {
    OPEN, CLOSE
  }

}
