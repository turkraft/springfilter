package com.torshid.springfilter.token.matcher;

import java.util.regex.Pattern;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.matcher.Matcher;
import com.torshid.springfilter.token.Field;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FieldMatcher extends Matcher<Field> {

  private static Pattern pattern = Pattern.compile("^[a-zA-Z]+[a-zA-Z0-9_$]*(?:\\.[a-zA-Z]+[a-zA-Z0-9_$]*)*");

  @Override
  public Field match(StringBuilder input) {

    String matched = input.getMatch(pattern);

    if (matched != null) {
      return Field.builder().name(matched).build();
    }

    return null;

  }

}
