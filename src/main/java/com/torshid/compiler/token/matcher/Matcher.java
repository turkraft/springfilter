package com.torshid.compiler.token.matcher;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.TokenizerException;
import com.torshid.compiler.token.IToken;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public abstract class Matcher<T extends IToken> {

  public abstract T match(StringBuilder input) throws TokenizerException;

}
