package com.turkraft.springfilter.definition;

import java.util.List;
import org.antlr.v4.runtime.misc.Pair;

public interface FilterOperators {

  List<FilterPrefixOperator> getPrefixOperators();

  List<FilterInfixOperator> getInfixOperators();

  List<FilterPostfixOperator> getPostfixOperators();

  default FilterPrefixOperator getPrefixOperator(String token) {
    for (FilterPrefixOperator prefixOperator : getPrefixOperators()) {
      for (String prefixOperatorToken : prefixOperator.getTokens()) {
        if (prefixOperatorToken.equalsIgnoreCase(token)) {
          return prefixOperator;
        }
      }
    }
    throw new UnsupportedOperationException("Unrecognized prefix operator `" + token + "`");
  }

  @SuppressWarnings("unchecked")
  default <T extends FilterPrefixOperator> T getPrefixOperator(Class<T> type) {
    return (T) getPrefixOperators().stream().filter(o -> type.isAssignableFrom(o.getClass()))
        .findFirst()
        .orElseThrow(() ->
            new UnsupportedOperationException(
                "Unrecognized operator " + type.getSimpleName()));
  }

  default FilterInfixOperator getInfixOperator(String token) {
    for (FilterInfixOperator infixOperator : getInfixOperators()) {
      for (String infixOperatorToken : infixOperator.getTokens()) {
        if (infixOperatorToken.equalsIgnoreCase(token)) {
          return infixOperator;
        }
      }
    }
    throw new UnsupportedOperationException("Unrecognized infix operator `" + token + "`");
  }

  @SuppressWarnings("unchecked")
  default <T extends FilterInfixOperator> T getInfixOperator(Class<T> type) {
    return (T) getInfixOperators().stream().filter(o -> type.isAssignableFrom(o.getClass()))
        .findFirst()
        .orElseThrow(() ->
            new UnsupportedOperationException(
                "Unrecognized operator " + type.getSimpleName()));
  }

  default FilterPostfixOperator getPostfixOperator(String token) {
    for (FilterPostfixOperator postfixOperator : getPostfixOperators()) {
      for (String postfixOperatorToken : postfixOperator.getTokens()) {
        if (postfixOperatorToken.equalsIgnoreCase(token)) {
          return postfixOperator;
        }
      }
    }
    throw new UnsupportedOperationException("Unrecognized postfix operator `" + token + "`");
  }

  @SuppressWarnings("unchecked")
  default <T extends FilterPostfixOperator> T getPostfixOperator(Class<T> type) {
    return (T) getPostfixOperators().stream().filter(o -> type.isAssignableFrom(o.getClass()))
        .findFirst()
        .orElseThrow(() ->
            new UnsupportedOperationException(
                "Unrecognized operator " + type.getSimpleName()));
  }

  List<Pair<FilterOperator, String>> getSortedOperators();

}
