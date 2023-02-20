package com.turkraft.springfilter.parser.node;

import java.util.List;

public class PriorityNode extends FilterNode {

  private final FilterNode node;

  public PriorityNode(FilterNode filterNode) {
    this.node = filterNode;
  }

  public FilterNode getNode() {
    return node;
  }

  @Override
  public List<FilterNode> getChildren() {
    return List.of(node);
  }

}
