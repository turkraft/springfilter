package com.turkraft.springfilter.compiler.token;

import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.exception.TokenizerException;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public abstract class Matcher<T extends IToken> {

  public abstract T match(StringBuilder input) throws TokenizerException;

}
