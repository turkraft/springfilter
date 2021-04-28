package com.turkraft.springfilter.compiler.token.input;

import com.turkraft.springfilter.compiler.token.IToken;

public interface IInput extends IToken {

  boolean canBe(Class<?> klass);

  Object getValueAs(Class<?> klass);

  Object getValue();

  String generate();

}
