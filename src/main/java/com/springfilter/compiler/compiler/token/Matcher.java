package com.springfilter.compiler.compiler.token;

import com.springfilter.compiler.compiler.Extensions;
import com.springfilter.compiler.compiler.exception.TokenizerException;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public abstract class Matcher<T extends IToken> {

  public abstract T match(StringBuilder input) throws TokenizerException;

}
