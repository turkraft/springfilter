package com.turkraft.springfilter.parser.node;

import java.util.List;

public class FieldNode extends FilterNode {

  private final String name;

  public FieldNode(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public List<FilterNode> getChildren() {
    return List.of();
  }
  
}
