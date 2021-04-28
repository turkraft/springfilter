package com.turkraft.springfilter.compiler;

import java.util.LinkedList;
import com.turkraft.springfilter.compiler.token.CommaMatcher;
import com.turkraft.springfilter.compiler.token.ComparatorMatcher;
import com.turkraft.springfilter.compiler.token.DotMatcher;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.compiler.token.Matcher;
import com.turkraft.springfilter.compiler.token.OperatorMatcher;
import com.turkraft.springfilter.compiler.token.ParenthesisMatcher;
import com.turkraft.springfilter.compiler.token.SpaceMatcher;
import com.turkraft.springfilter.compiler.token.WordMatcher;
import com.turkraft.springfilter.compiler.token.input.BoolMatcher;
import com.turkraft.springfilter.compiler.token.input.NumeralMatcher;
import com.turkraft.springfilter.compiler.token.input.TextMatcher;
import com.turkraft.springfilter.exception.InvalidInputException;
import com.turkraft.springfilter.exception.TokenizerException;

public class Tokenizer {

  private Tokenizer() {}

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      new SpaceMatcher(), new DotMatcher(), new CommaMatcher(), new ParenthesisMatcher(),
      new OperatorMatcher(), new ComparatorMatcher(), new BoolMatcher(), new WordMatcher(),
      new TextMatcher(), new NumeralMatcher()

  };

  public static LinkedList<IToken> tokenize(String input) throws TokenizerException {

    LinkedList<IToken> tokens = new LinkedList<>();

    StringBuilder sb = new StringBuilder(input);

    while (sb.length() > 0) {

      boolean consumed = false;

      int currentLength = sb.length();

      for (Matcher<?> matcher : matchers) {

        IToken token = matcher.match(sb);

        if (sb.length() != currentLength) {

          if (token != null) {
            tokens.add(token);
          }

          consumed = true;
          break;

        }

      }

      if (!consumed) {
        throw new InvalidInputException(sb.toString());
      }

    }

    return tokens;

  }

}
