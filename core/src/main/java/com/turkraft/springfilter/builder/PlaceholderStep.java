package com.turkraft.springfilter.builder;


import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.definition.FilterPlaceholder;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.PlaceholderNode;

public interface PlaceholderStep extends Step {

  default PlaceholderStepImpl placeholder(FilterPlaceholder placeholder) {
    return new PlaceholderStepImpl(getOperators(), getFilterStringConverter(),
        new PlaceholderNode(placeholder));
  }

  class PlaceholderStepImpl extends StepWithResultImpl implements ComparisonStep, LogicStep {

    PlaceholderStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
