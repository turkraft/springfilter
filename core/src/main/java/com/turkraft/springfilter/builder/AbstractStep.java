package com.turkraft.springfilter.builder;


import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.Objects;

public abstract class AbstractStep implements Step {

  private final FilterOperators operators;

  private final FilterStringConverter filterStringConverter;

  public AbstractStep(FilterOperators operators, FilterStringConverter filterStringConverter) {
    this.operators = operators;
    this.filterStringConverter = filterStringConverter;
  }

  @Override
  public FilterOperators getOperators() {
    return operators;
  }

  @Override
  public FilterStringConverter getFilterStringConverter() {
    return filterStringConverter;
  }

  static class StepWithResultImpl extends AbstractStep implements StepWithResult {

    private final FilterNode result;

    public StepWithResultImpl(FilterOperators operators,
        FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter);
      this.result = result;
    }

    public FilterNode get() {
      return result;
    }

    @Override
    public String toString() {
      return generate();
    }

    public String generate() {
      return Objects.requireNonNull(getFilterStringConverter().convert(get()),
          "Could not find filter node to string converter!");
    }

  }

}
