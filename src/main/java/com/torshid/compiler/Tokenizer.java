package com.torshid.compiler;

import java.util.LinkedList;

import com.torshid.compiler.exception.InvalidInputException;
import com.torshid.compiler.exception.TokenizerException;
import com.torshid.compiler.token.Token;
import com.torshid.compiler.token.matcher.Matcher;

public class Tokenizer {

  private Tokenizer() {}

  public static LinkedList<Token> tokenize(Matcher<?>[] matchers, String input) throws TokenizerException {

    LinkedList<Token> tokens = new LinkedList<>();

    StringBuilder sb = new StringBuilder(input);

    while (sb.length() > 0) {

      boolean consumed = false;

      int currentLength = sb.length();

      for (Matcher<?> matcher : matchers) {

        Token token = matcher.match(sb);

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
