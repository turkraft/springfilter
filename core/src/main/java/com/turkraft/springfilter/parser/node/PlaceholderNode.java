package com.turkraft.springfilter.parser.node;

import com.turkraft.springfilter.definition.FilterPlaceholder;
import java.util.List;

public class PlaceholderNode extends FilterNode {

  private final FilterPlaceholder placeholder;

  public PlaceholderNode(FilterPlaceholder placeholder) {
    this.placeholder = placeholder;
  }

  public FilterPlaceholder getPlaceholder() {
    return placeholder;
  }

  @Override
  public List<FilterNode> getChildren() {
    return List.of();
  }
  
}
