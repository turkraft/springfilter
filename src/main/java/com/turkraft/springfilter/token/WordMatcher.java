package com.turkraft.springfilter.token;

import java.util.regex.Pattern;
import com.turkraft.springfilter.FilterExtensions;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(FilterExtensions.class)
public class WordMatcher extends Matcher<Word> {

  public static Pattern PATTERN = Pattern.compile("^[\\p{L}_][\\p{L}0-9_]*");

  @Override
  public Word match(StringBuilder input) {

    String matched = input.getMatch(PATTERN);

    if (matched != null) {
      return Word.builder().value(matched).build();
    }

    return null;

  }

}
