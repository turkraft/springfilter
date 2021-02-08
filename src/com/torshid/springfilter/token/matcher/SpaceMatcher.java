package com.torshid.springfilter.token.matcher;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.Token;
import com.torshid.compiler.token.matcher.Matcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class SpaceMatcher extends Matcher<Token> {

  @Override
  public Token match(StringBuilder input) {

    // just skip unnecessary spaces

    while (input.indexIs(' ') || input.indexIs('\t')) {
      input.take();
    }

    return null;

  }

}
