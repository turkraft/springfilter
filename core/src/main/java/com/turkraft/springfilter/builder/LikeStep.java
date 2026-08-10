package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.parser.node.CollectionLikeNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InputNode;
import java.util.Arrays;
import java.util.stream.Collectors;

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

  default LikeCollectionStepImpl likeCollection(StepWithResult... patterns) {
    return new LikeCollectionStepImpl(getOperators(),
        new CollectionLikeNode(get(),
            new LikeOperator(),
            Arrays.stream(patterns).map(StepWithResult::get).collect(Collectors.toList())));
  }

  class LikeStepImpl extends StepWithResultImpl implements LogicStep {

    LikeStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

  class LikeCollectionStepImpl extends StepWithResultImpl implements LogicStep {

    LikeCollectionStepImpl(FilterOperators operators, FilterNode result) {
      super(operators, result);
    }

  }

}
