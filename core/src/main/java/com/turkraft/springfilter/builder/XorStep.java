package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.XorOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface XorStep extends StepWithResult {

  default XorStepImpl xor(StepWithResult with) {
    return new XorStepImpl(getOperators(),
        get().infix(
            getOperators().getInfixOperator(XorOperator.class),
            with.get()));
  }

  class XorStepImpl extends StepWithResultImpl implements LogicStep {

    XorStepImpl(FilterOperators operators, FilterNode result) {
      super(operators, result);
    }

  }

}
