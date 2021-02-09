package com.torshid.springfilter.token.matcher.input;


import java.util.regex.Pattern;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.OutOfInputException;
import com.torshid.compiler.token.matcher.Matcher;
import com.torshid.springfilter.token.input.Text;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class TextMatcher extends Matcher<Text> {

  private static Pattern pattern = Pattern.compile("^[\\p{L}0-9]+");

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

      return Text.builder()
          .value(chars)
          .build();

    }

    // we may also accept characters which are not inside quotes if they consist of only letters and digits

    String matched = input.getMatch(pattern);

    if (matched != null) {
      return Text.builder()
          .value(matched)
          .build();
    }

    return null;

  }

}
