package com.torshid.springfilter.token.input;

import com.torshid.compiler.token.IToken;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Input<T> implements IToken {

  private T value;

  public abstract Object getValueAs(Class<?> klass);

}
