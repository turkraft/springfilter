package com.springfilter.compiler.springfilter;

import java.util.LinkedList;

import com.springfilter.compiler.compiler.Tokenizer;
import com.springfilter.compiler.compiler.exception.TokenizerException;
import com.springfilter.compiler.compiler.token.IToken;
import com.springfilter.compiler.compiler.token.Matcher;
import com.springfilter.compiler.springfilter.token.ComparatorMatcher;
import com.springfilter.compiler.springfilter.token.DotMatcher;
import com.springfilter.compiler.springfilter.token.OperatorMatcher;
import com.springfilter.compiler.springfilter.token.ParenthesisMatcher;
import com.springfilter.compiler.springfilter.token.SpaceMatcher;
import com.springfilter.compiler.springfilter.token.WordMatcher;
import com.springfilter.compiler.springfilter.token.input.BoolMatcher;
import com.springfilter.compiler.springfilter.token.input.NumeralMatcher;
import com.springfilter.compiler.springfilter.token.input.TextMatcher;

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
