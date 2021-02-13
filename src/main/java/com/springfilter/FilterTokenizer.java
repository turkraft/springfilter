package com.springfilter;

import java.util.LinkedList;

import com.springfilter.compiler.Tokenizer;
import com.springfilter.compiler.exception.TokenizerException;
import com.springfilter.compiler.token.IToken;
import com.springfilter.compiler.token.Matcher;
import com.springfilter.token.CommaMatcher;
import com.springfilter.token.ComparatorMatcher;
import com.springfilter.token.DotMatcher;
import com.springfilter.token.OperatorMatcher;
import com.springfilter.token.ParenthesisMatcher;
import com.springfilter.token.SpaceMatcher;
import com.springfilter.token.WordMatcher;
import com.springfilter.token.input.BoolMatcher;
import com.springfilter.token.input.NumeralMatcher;
import com.springfilter.token.input.TextMatcher;

public class FilterTokenizer {

  private FilterTokenizer() {}

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      new SpaceMatcher(), new DotMatcher(), new CommaMatcher(), new ParenthesisMatcher(), new OperatorMatcher(),
      new ComparatorMatcher(), new BoolMatcher(), new WordMatcher(), new TextMatcher(), new NumeralMatcher()

  };

  public static LinkedList<IToken> tokenize(String input) throws TokenizerException {
    return Tokenizer.tokenize(matchers, input);
  }

}
