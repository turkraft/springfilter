package com.turkraft.springfilter.builder;


import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.PriorityNode;

public interface PriorityStep extends Step {

  default PriorityStepImpl priority(StepWithResult value) {
    return new PriorityStepImpl(getOperators(),
        new PriorityNode(value.get()));
  }

  class PriorityStepImpl extends StepWithResultImpl implements LogicStep {

    PriorityStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
