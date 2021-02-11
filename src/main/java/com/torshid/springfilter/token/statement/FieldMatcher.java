package com.torshid.springfilter.token.statement;

import java.util.regex.Pattern;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.Matcher;
import com.torshid.springfilter.token.WordMatcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FieldMatcher extends Matcher<Field> {

  private static Pattern pattern =
      Pattern.compile(WordMatcher.PATTERN.pattern() + "(?:\\." + WordMatcher.PATTERN.pattern().substring(1) + ")*");

  @Override
  public Field match(StringBuilder input) {

    String matched = input.getMatch(pattern);

    if (matched != null) {
      return Field.builder().name(matched).build();
    }

    return null;

  }

}
