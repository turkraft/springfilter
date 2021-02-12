package com.springfilter.compiler.springfilter.token.input;


import com.springfilter.compiler.compiler.Extensions;
import com.springfilter.compiler.compiler.exception.OutOfInputException;
import com.springfilter.compiler.compiler.token.Matcher;

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
