package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.AndOperator;
import com.turkraft.springfilter.language.GreaterThanOrEqualOperator;
import com.turkraft.springfilter.language.LessThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;

public interface BetweenStep extends StepWithResult {

  default BetweenStepImpl between(StepWithResult lower, StepWithResult upper) {
    FilterOperators ops = getOperators();
    FilterNode gte = new InfixOperationNode(
        get(),
        ops.getInfixOperator(GreaterThanOrEqualOperator.class),
        lower.get());
    FilterNode lte = new InfixOperationNode(
        get(),
        ops.getInfixOperator(LessThanOrEqualOperator.class),
        upper.get());
    return new BetweenStepImpl(ops, gte.infix(ops.getInfixOperator(
        AndOperator.class), lte));
  }

  class BetweenStepImpl extends StepWithResultImpl implements LogicStep {

    BetweenStepImpl(FilterOperators operators, FilterNode result) {
      super(operators, result);
    }

  }

}
