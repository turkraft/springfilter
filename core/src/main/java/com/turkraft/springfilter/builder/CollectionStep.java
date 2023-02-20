package com.turkraft.springfilter.builder;


import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.parser.node.CollectionNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface CollectionStep extends Step {

  default CollectionStepImpl collection(List<StepWithResult> items) {
    return new CollectionStepImpl(getOperators(), getFilterStringConverter(),
        new CollectionNode(items.stream().map(StepWithResult::get).collect(Collectors.toList())));
  }

  default CollectionStepImpl collection(StepWithResult... arguments) {
    return collection(Arrays.asList(arguments));
  }

  class CollectionStepImpl extends StepWithResultImpl implements ComparisonStep {

    CollectionStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
