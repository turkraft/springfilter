package com.torshid.springfilter;

import java.util.LinkedList;

import com.torshid.compiler.Tokenizer;
import com.torshid.compiler.exception.TokenizerException;
import com.torshid.compiler.token.Token;
import com.torshid.compiler.token.matcher.Matcher;
import com.torshid.springfilter.token.matcher.ComparatorMatcher;
import com.torshid.springfilter.token.matcher.FieldMatcher;
import com.torshid.springfilter.token.matcher.OperatorMatcher;
import com.torshid.springfilter.token.matcher.ParenthesisMatcher;
import com.torshid.springfilter.token.matcher.SpaceMatcher;
import com.torshid.springfilter.token.matcher.input.BoolMatcher;
import com.torshid.springfilter.token.matcher.input.NumeralMatcher;
import com.torshid.springfilter.token.matcher.input.TextMatcher;

public class FilterTokenizer {

  private FilterTokenizer() {}

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      new SpaceMatcher(), new ParenthesisMatcher(), new OperatorMatcher(), new BoolMatcher(), new ComparatorMatcher(),
      new FieldMatcher(), new NumeralMatcher(), new TextMatcher()

  };

  public static LinkedList<Token> tokenize(String input) throws TokenizerException {
    return Tokenizer.tokenize(matchers, input);
  }

}
