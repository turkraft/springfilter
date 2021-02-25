package com.turkraft.springfilter.token.input;


import com.turkraft.springfilter.FilterExtensions;
import com.turkraft.springfilter.exception.OutOfInputException;
import com.turkraft.springfilter.token.Matcher;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(FilterExtensions.class)
public class TextMatcher extends Matcher<Text> {

  @Override
  public Text match(StringBuilder input) throws OutOfInputException {

    if (input.indexIs('\'')) {

      String chars = "";

      input.take();

      while (!input.isEmpty() && !input.indexIs('\'')) {

        chars += input.take();

        if (input.indexIs('\\', '\'')) {
          chars += input.take();
          chars += input.take();
        }

      }

      if (input.isEmpty()) {
        throw new OutOfInputException("Reached end of input before end of text " + chars);
      }

      input.take();

      return Text.builder().value(chars.replace("\\'", "'")).build();

    }

    return null;

  }

}
