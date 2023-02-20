package com.turkraft.springfilter.parser.node;


import java.util.List;

public class InputNode extends FilterNode {

  private final Object value;

  public InputNode(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public List<FilterNode> getChildren() {
    return List.of();
  }
  
}
