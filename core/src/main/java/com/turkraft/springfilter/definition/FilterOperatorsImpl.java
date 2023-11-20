package com.turkraft.springfilter.definition;

import java.util.LinkedList;
import java.util.List;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Component;

@Component
class FilterOperatorsImpl implements FilterOperators {

  private final List<FilterPrefixOperator> prefixOperators;

  private final List<FilterInfixOperator> infixOperators;

  private final List<FilterPostfixOperator> postfixOperators;

  private final List<Pair<FilterOperator, String>> sortedOperators;

  public FilterOperatorsImpl(List<FilterPrefixOperator> prefixOperators,
      List<FilterInfixOperator> infixOperators,
      List<FilterPostfixOperator> postfixOperators) {
    this.prefixOperators = prefixOperators;
    this.infixOperators = infixOperators;
    this.postfixOperators = postfixOperators;
    sortedOperators = new LinkedList<>();
    getPrefixOperators().forEach(this::addSortedOperator);
    getInfixOperators().forEach(this::addSortedOperator);
    getPostfixOperators().forEach(this::addSortedOperator);
    sortedOperators.sort((o1, o2) -> -Integer.compare(o1.a.getPriority(), o2.a.getPriority()));
    sortedOperators.sort(
        (o1, o2) -> -Integer.compare(o1.b.length(), o2.b.length()));
  }

  private void addSortedOperator(FilterOperator operator) {
    for (String token : operator.getTokens()) {
      sortedOperators.add(new Pair<>(operator, token.toLowerCase()));
    }
  }

  @Override
  public List<FilterPrefixOperator> getPrefixOperators() {
    return prefixOperators;
  }

  @Override
  public List<FilterInfixOperator> getInfixOperators() {
    return infixOperators;
  }

  @Override
  public List<FilterPostfixOperator> getPostfixOperators() {
    return postfixOperators;
  }

  @Override
  public List<Pair<FilterOperator, String>> getSortedOperators() {
    return sortedOperators;
  }

}
