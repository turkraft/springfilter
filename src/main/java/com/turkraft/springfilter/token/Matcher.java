package com.turkraft.springfilter.token;

import com.turkraft.springfilter.FilterExtensions;
import com.turkraft.springfilter.exception.TokenizerException;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(FilterExtensions.class)
public abstract class Matcher<T extends IToken> {

  public abstract T match(StringBuilder input) throws TokenizerException;

}
