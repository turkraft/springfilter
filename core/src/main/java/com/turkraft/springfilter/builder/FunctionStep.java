package com.turkraft.springfilter.builder;


import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterFunction;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.FunctionNode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface FunctionStep extends Step {

  default FunctionStepImpl function(FilterFunction function, List<StepWithResult> arguments) {
    return new FunctionStepImpl(getOperators(), getFilterStringConverter(),
        new FunctionNode(function,
            arguments.stream().map(StepWithResult::get).collect(Collectors.toList())));
  }

  default FunctionStepImpl function(FilterFunction function, StepWithResult... arguments) {
    return function(function, Arrays.asList(arguments));
  }

  class FunctionStepImpl extends StepWithResultImpl implements ComparisonStep, LogicStep {

    FunctionStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
