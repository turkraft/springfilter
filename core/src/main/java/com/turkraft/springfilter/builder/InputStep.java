package com.turkraft.springfilter.builder;


import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InputNode;

public interface InputStep extends Step {

  default InputStepImpl input(Object value) {
    return new InputStepImpl(getOperators(), new InputNode(value));
  }

  class InputStepImpl extends StepWithResultImpl implements ComparisonStep {

    InputStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
