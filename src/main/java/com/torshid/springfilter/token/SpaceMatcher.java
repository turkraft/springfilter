package com.torshid.springfilter.token;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.IToken;
import com.torshid.compiler.token.Matcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class SpaceMatcher extends Matcher<IToken> {

  @Override
  public IToken match(StringBuilder input) {

    // just skip unnecessary spaces

    while (input.indexIs(' ') || input.indexIs('\t')) {
      input.take();
    }

    return null;

  }

}
