package com.turkraft.springfilter.builder;


import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.parser.node.FilterNode;

public abstract class AbstractStep implements Step {

  private final FilterOperators operators;
  
  public AbstractStep(FilterOperators operators) {
    this.operators = operators;
  }

  @Override
  public FilterOperators getOperators() {
    return operators;
  }

  static class StepWithResultImpl extends AbstractStep implements StepWithResult {

    private final FilterNode result;

    public StepWithResultImpl(FilterOperators operators,
        FilterNode result) {
      super(operators);
      this.result = result;
    }

    public FilterNode get() {
      return result;
    }

  }

}
