package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.IsEmptyOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface IsEmptyStep extends StepWithResult {

  default IsEmptyStepImpl isEmpty() {
    return new IsEmptyStepImpl(getOperators(), getFilterStringConverter(),
        get().postfix(getOperators().getPostfixOperator(IsEmptyOperator.class)));
  }

  class IsEmptyStepImpl extends StepWithResultImpl implements LogicStep {

    IsEmptyStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
