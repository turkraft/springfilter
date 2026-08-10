package com.turkraft.springfilter.typesafe.builder;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.language.AndOperator;
import com.turkraft.springfilter.language.OrOperator;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import java.util.ArrayList;
import java.util.List;

public abstract class FilterChain<P extends FilterChain<P>> {

  protected final FilterBuilder fb;
  private final List<FilterNode> conditions = new ArrayList<>();
  private final List<Connector> connectors = new ArrayList<>();
  private Connector pendingConnector = Connector.AND;

  protected FilterChain(FilterBuilder fb) {
    if (fb == null) {
      throw new IllegalArgumentException("FilterBuilder must not be null");
    }
    this.fb = fb;
  }

  void apply(FilterNode condition) {
    if (!conditions.isEmpty()) {
      connectors.add(pendingConnector);
    }
    conditions.add(condition);
    pendingConnector = Connector.AND;
  }

  @SuppressWarnings("unchecked")
  public P and() {
    pendingConnector = Connector.AND;
    return (P) this;
  }

  @SuppressWarnings("unchecked")
  public P or() {
    pendingConnector = Connector.OR;
    return (P) this;
  }

  public FilterNode build() {
    if (conditions.isEmpty()) {
      throw new IllegalStateException(
          "No conditions added to filter chain. Add at least one condition before calling build().");
    }
    if (conditions.size() == 1) {
      return conditions.get(0);
    }
    return buildWithPrecedence();
  }

  private FilterNode buildWithPrecedence() {
    List<FilterNode> orGroups = new ArrayList<>();
    List<FilterNode> currentAndGroup = new ArrayList<>();
    currentAndGroup.add(conditions.get(0));

    for (int i = 0; i < connectors.size(); i++) {
      if (connectors.get(i) == Connector.AND) {
        currentAndGroup.add(conditions.get(i + 1));
      } else {
        orGroups.add(combineAnd(currentAndGroup));
        currentAndGroup.clear();
        currentAndGroup.add(conditions.get(i + 1));
      }
    }
    orGroups.add(combineAnd(currentAndGroup));

    FilterNode result = orGroups.get(0);
    for (int i = 1; i < orGroups.size(); i++) {
      result = new InfixOperationNode(result,
          fb.getOperators().getInfixOperator(OrOperator.class), orGroups.get(i));
    }
    return result;
  }

  private FilterNode combineAnd(List<FilterNode> nodes) {
    if (nodes.size() == 1) {
      return nodes.get(0);
    }
    FilterNode result = nodes.get(0);
    for (int i = 1; i < nodes.size(); i++) {
      result = new InfixOperationNode(result,
          fb.getOperators().getInfixOperator(AndOperator.class), nodes.get(i));
    }
    return result;
  }

  public FilterBuilder getFilterBuilder() {
    return fb;
  }

  private enum Connector {
    AND, OR
  }

}
