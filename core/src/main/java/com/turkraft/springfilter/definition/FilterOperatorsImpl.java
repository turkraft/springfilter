package com.turkraft.springfilter.definition;

import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class FilterOperatorsImpl implements FilterOperators {

  private final List<FilterPrefixOperator> prefixOperators;

  private final List<FilterInfixOperator> infixOperators;

  private final List<FilterPostfixOperator> postfixOperators;

  private final List<FilterOperator> sortedOperators;

  public FilterOperatorsImpl(List<FilterPrefixOperator> prefixOperators,
      List<FilterInfixOperator> infixOperators,
      List<FilterPostfixOperator> postfixOperators) {
    this.prefixOperators = prefixOperators;
    this.infixOperators = infixOperators;
    this.postfixOperators = postfixOperators;
    sortedOperators = new LinkedList<>();
    sortedOperators.addAll(getPrefixOperators());
    sortedOperators.addAll(getInfixOperators());
    sortedOperators.addAll(getPostfixOperators());
    sortedOperators.sort((o1, o2) -> -Integer.compare(o1.getPriority(), o2.getPriority()));
    sortedOperators.sort(
        (o1, o2) -> -Integer.compare(o1.getToken().length(), o2.getToken().length()));
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
  public List<FilterOperator> getSortedOperators() {
    return sortedOperators;
  }

}
