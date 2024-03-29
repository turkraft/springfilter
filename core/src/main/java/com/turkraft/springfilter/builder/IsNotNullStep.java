package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.IsNotNullOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface IsNotNullStep extends StepWithResult {

  default IsNotNullStepImpl isNotNull() {
    return new IsNotNullStepImpl(getOperators(),
        get().postfix(getOperators().getPostfixOperator(IsNotNullOperator.class)));
  }

  class IsNotNullStepImpl extends StepWithResultImpl implements LogicStep {

    IsNotNullStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
