package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.IsNotEmptyOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface IsNotEmptyStep extends StepWithResult {

  default IsNotEmptyStepImpl isNotEmpty() {
    return new IsNotEmptyStepImpl(getOperators(), getFilterStringConverter(),
        get().postfix(getOperators().getPostfixOperator(IsNotEmptyOperator.class)));
  }

  class IsNotEmptyStepImpl extends StepWithResultImpl implements LogicStep {

    IsNotEmptyStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
