package com.springfilter.token.input;

import com.springfilter.compiler.token.IToken;

public interface IInput extends IToken {

  boolean canBe(Class<?> klass);

  Object getValueAs(Class<?> klass);

  String toStringAs(Class<?> klass);

}
