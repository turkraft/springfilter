package com.turkraft.springfilter.compiler.token;

import java.util.regex.Pattern;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.token.LiteralMatcher.ILiteral;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public abstract class LiteralMatcher<T extends Enum<T> & IToken & ILiteral> extends Matcher<T> {

  @Override
  public T match(StringBuilder input) {

    for (T type : getEnumClass().getEnumConstants()) {

      ILiteral literal = type;

      if (literal.getRegex() == null) {

        // match using literal

        if (input.length() >= literal.getLiteral().length()
            && input.substring(0, literal.getLiteral().length()).toLowerCase()
                .equalsIgnoreCase(literal.getLiteral())) {

          input.take(literal.getLiteral().length());

          return type;

        }

      } else {

        // match using regex

        String match =
            input.getMatch(Pattern.compile(literal.getRegex(), Pattern.CASE_INSENSITIVE));

        if (match != null) {
          return type;
        }

      }

    }

    return null;

  }

  public abstract Class<T> getEnumClass();

  public interface ILiteral {

    String getLiteral();

    String getRegex();

  }

}
