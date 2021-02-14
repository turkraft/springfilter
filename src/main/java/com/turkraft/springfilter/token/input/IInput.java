package com.turkraft.springfilter.token.input;

import com.turkraft.springfilter.token.IToken;

public interface IInput extends IToken {

  boolean canBe(Class<?> klass);

  Object getValueAs(Class<?> klass);

  String toStringAs(Class<?> klass);

}
