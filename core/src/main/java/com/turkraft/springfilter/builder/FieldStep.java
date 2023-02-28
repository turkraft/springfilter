package com.turkraft.springfilter.builder;


import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface FieldStep extends Step {

  default FieldStepImpl field(String name) {
    return new FieldStepImpl(getOperators(), new FieldNode(name));
  }

  class FieldStepImpl extends StepWithResultImpl implements ComparisonStep, LogicStep {

    FieldStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
