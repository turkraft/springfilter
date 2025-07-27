package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.InsensitiveLikeOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface InsensitiveLikeStep extends StepWithResult {

  default InsensitiveLikeStepImpl insensitiveLike(StepWithResult to) {
    return new InsensitiveLikeStepImpl(getOperators(),
        get().infix(getOperators().getInfixOperator(InsensitiveLikeOperator.class),
            to.get()));
  }

  class InsensitiveLikeStepImpl extends StepWithResultImpl implements LogicStep {

    InsensitiveLikeStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
