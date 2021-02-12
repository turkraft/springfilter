package com.springfilter.compiler.springfilter.token.input;

import com.springfilter.compiler.compiler.token.IToken;

public interface IInput extends IToken {

  boolean canBe(Class<?> klass);

  Object getValueAs(Class<?> klass);

  String toStringAs(Class<?> klass);

}
