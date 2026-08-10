package com.turkraft.springfilter.parser.node;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import java.util.List;

public class CollectionLikeNode extends FilterNode {

  private final FilterNode left;
  private final FilterInfixOperator operator;
  private final List<FilterNode> patterns;

  public CollectionLikeNode(FilterNode left, FilterInfixOperator operator, List<FilterNode> patterns) {
    this.left = left;
    this.operator = operator;
    this.patterns = patterns;
  }

  public FilterNode getLeft() {
    return left;
  }

  public FilterInfixOperator getOperator() {
    return operator;
  }

  public List<FilterNode> getPatterns() {
    return patterns;
  }

  @Override
  public List<FilterNode> getChildren() {
    return List.of(left);
  }

}
