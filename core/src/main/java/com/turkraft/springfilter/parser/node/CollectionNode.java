package com.turkraft.springfilter.parser.node;

import java.util.List;

public class CollectionNode extends FilterNode {

  private final List<FilterNode> items;

  public CollectionNode(List<FilterNode> items) {
    this.items = items;
  }

  public List<FilterNode> getItems() {
    return items;
  }

  @Override
  public List<FilterNode> getChildren() {
    return getItems();
  }
  
}
