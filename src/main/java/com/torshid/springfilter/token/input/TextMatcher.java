package com.torshid.springfilter.token.input;


import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.OutOfInputException;
import com.torshid.compiler.token.Matcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class TextMatcher extends Matcher<Text> {

  @Override
  public Text match(StringBuilder input) throws OutOfInputException {

    if (input.indexIs('\'')) {

      String chars = "";

      input.take();

      while (!input.indexIs('\'')) {
        chars += input.take();
      }

      if (input.isEmpty()) {
        throw new OutOfInputException("Reached end of input before end of text " + chars);
      }

      input.take();

      return Text.builder().value(chars).build();

    }

    return null;

  }

}
