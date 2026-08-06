package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.InsensitiveLikeOperator;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InputNode;

public interface InsensitiveLikeStep extends StepWithResult {

  default InsensitiveLikeStepImpl insensitiveLike(StepWithResult to) {
    return new InsensitiveLikeStepImpl(getOperators(),
        get().infix(getOperators().getInfixOperator(InsensitiveLikeOperator.class),
            to.get()));
  }

  default InsensitiveLikeStepImpl insensitiveStartsWith(String value) {
    return insensitiveLike(new StepWithResultImpl(getOperators(), new InputNode(value + "%")));
  }

  default InsensitiveLikeStepImpl insensitiveEndsWith(String value) {
    return insensitiveLike(new StepWithResultImpl(getOperators(), new InputNode("%" + value)));
  }

  default InsensitiveLikeStepImpl insensitiveContains(String value) {
    return insensitiveLike(new StepWithResultImpl(getOperators(), new InputNode("%" + value + "%")));
  }

  class InsensitiveLikeStepImpl extends StepWithResultImpl implements LogicStep {

    InsensitiveLikeStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
