package com.turkraft.springfilter;

import java.util.LinkedList;

import com.turkraft.springfilter.compiler.Tokenizer;
import com.turkraft.springfilter.compiler.exception.TokenizerException;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.compiler.token.Matcher;
import com.turkraft.springfilter.token.CommaMatcher;
import com.turkraft.springfilter.token.ComparatorMatcher;
import com.turkraft.springfilter.token.DotMatcher;
import com.turkraft.springfilter.token.OperatorMatcher;
import com.turkraft.springfilter.token.ParenthesisMatcher;
import com.turkraft.springfilter.token.SpaceMatcher;
import com.turkraft.springfilter.token.WordMatcher;
import com.turkraft.springfilter.token.input.BoolMatcher;
import com.turkraft.springfilter.token.input.NumeralMatcher;
import com.turkraft.springfilter.token.input.TextMatcher;

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
