package com.torshid.springfilter;

import java.util.LinkedList;

import com.torshid.compiler.Tokenizer;
import com.torshid.compiler.exception.TokenizerException;
import com.torshid.compiler.token.IToken;
import com.torshid.compiler.token.Matcher;
import com.torshid.springfilter.token.ComparatorMatcher;
import com.torshid.springfilter.token.DotMatcher;
import com.torshid.springfilter.token.OperatorMatcher;
import com.torshid.springfilter.token.ParenthesisMatcher;
import com.torshid.springfilter.token.SpaceMatcher;
import com.torshid.springfilter.token.WordMatcher;
import com.torshid.springfilter.token.input.BoolMatcher;
import com.torshid.springfilter.token.input.NumeralMatcher;
import com.torshid.springfilter.token.input.TextMatcher;

public class FilterTokenizer {

  private FilterTokenizer() {}

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      new SpaceMatcher(), new DotMatcher(), new ParenthesisMatcher(), new OperatorMatcher(), new ComparatorMatcher(),
      new BoolMatcher(), new WordMatcher(), new TextMatcher(), new NumeralMatcher()

  };

  public static LinkedList<IToken> tokenize(String input) throws TokenizerException {
    return Tokenizer.tokenize(matchers, input);
  }

}
