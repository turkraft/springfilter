package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InputNode;

public interface LikeStep extends StepWithResult {

  default LikeStepImpl like(StepWithResult to) {
    return new LikeStepImpl(getOperators(),
        get().infix(getOperators().getInfixOperator(LikeOperator.class),
            to.get()));
  }

  default LikeStepImpl startsWith(String value) {
    return like(new StepWithResultImpl(getOperators(), new InputNode(value + "%")));
  }

  default LikeStepImpl endsWith(String value) {
    return like(new StepWithResultImpl(getOperators(), new InputNode("%" + value)));
  }

  default LikeStepImpl contains(String value) {
    return like(new StepWithResultImpl(getOperators(), new InputNode("%" + value + "%")));
  }

  class LikeStepImpl extends StepWithResultImpl implements LogicStep {

    LikeStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
