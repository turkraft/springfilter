package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface LikeStep extends StepWithResult {

  default LikeStepImpl like(StepWithResult to) {
    return new LikeStepImpl(getOperators(), getFilterStringConverter(),
        get().infix(getOperators().getInfixOperator(LikeOperator.class),
            to.get()));
  }

  class LikeStepImpl extends StepWithResultImpl implements LogicStep {

    LikeStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
