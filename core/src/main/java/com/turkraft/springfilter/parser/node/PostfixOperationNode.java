package com.turkraft.springfilter.parser.node;

import com.turkraft.springfilter.definition.FilterPostfixOperator;
import java.util.List;

public class PostfixOperationNode extends OperationNode {

  private final FilterNode left;

  public PostfixOperationNode(FilterNode left, FilterPostfixOperator operator) {
    super(operator);
    this.left = left;
  }

  public FilterNode getLeft() {
    return left;
  }

  @Override
  public FilterPostfixOperator getOperator() {
    return (FilterPostfixOperator) super.getOperator();
  }

  @Override
  public List<FilterNode> getChildren() {
    return List.of(left);
  }
  
}
