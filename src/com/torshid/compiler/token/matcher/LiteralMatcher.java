package com.torshid.compiler.token.matcher;

import java.util.regex.Pattern;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.Token;
import com.torshid.compiler.token.matcher.LiteralMatcher.ILiteral;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public abstract class LiteralMatcher<T extends Token, E extends Enum<E> & ILiteral> extends Matcher<T> {

  @Override
  public T match(StringBuilder input) {

    for (Enum<E> type : getEnumClass().getEnumConstants()) {

      ILiteral literal = (ILiteral) type;

      if (literal.getRegex() == null) {

        // match using literal

        if (input.length() >= literal.getLiteral().length()
            && input.substring(0, literal.getLiteral().length()).toLowerCase().equalsIgnoreCase(literal.getLiteral())) {

          input.take(literal.getLiteral().length());

          return enumToToken(type);

        }

      } else {

        // match using regex

        String match = input.getMatch(Pattern.compile(literal.getRegex()));

        if (match != null) {
          return enumToToken(type);
        }

      }

    }

    return null;

  }

  public abstract Class<E> getEnumClass();

  public abstract T enumToToken(Enum<E> type);

  public interface ILiteral {

    String getLiteral();

    String getRegex();

  }

}
