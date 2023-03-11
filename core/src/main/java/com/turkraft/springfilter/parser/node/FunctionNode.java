package com.turkraft.springfilter.parser.node;

import com.turkraft.springfilter.definition.FilterFunction;
import java.util.List;

public class FunctionNode extends FilterNode {

  private final FilterFunction filterFunction;

  private final List<FilterNode> arguments;

  public FunctionNode(FilterFunction filterFunction, List<FilterNode> arguments) {
    this.filterFunction = filterFunction;
    this.arguments = arguments;
  }

  public FilterFunction getFunction() {
    return filterFunction;
  }

  public List<FilterNode> getArguments() {
    return arguments;
  }

  @Override
  public List<FilterNode> getChildren() {
    return getArguments();
  }

  public FilterNode getArgument(int index, String exceptionMessage) {
    if (getArguments().size() <= index) {
      throw new IllegalArgumentException(exceptionMessage);
    }
    return getArguments().get(index);
  }

  public FilterNode getArgument(int index) {
    return getArgument(index,
        "The function `" + filterFunction.getName() + "` expects at least " + index
            + " argument(s)");
  }

}
