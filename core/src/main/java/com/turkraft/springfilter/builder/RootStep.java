package com.turkraft.springfilter.builder;


import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.AndOperator;
import com.turkraft.springfilter.language.OrOperator;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.lang.Nullable;

public class RootStep extends AbstractStep
    implements FieldStep, InputStep, CollectionStep, FunctionStep, PriorityStep,
    PlaceholderStep {

  public RootStep(FilterOperators operators, FilterStringConverter filterStringConverter) {
    super(operators);
  }

  public StepWithResult from(FilterNode node) {
    return new StepWithResultImpl(getOperators(), node);
  }

  private StepWithResult and(@Nullable StepWithResult left, @Nullable StepWithResult right) {
    if (left == null && right == null) {
      throw new IllegalArgumentException();
    }
    if (left == null) {
      return right;
    }
    if (right == null) {
      return left;
    }
    return new StepWithResultImpl(getOperators(),
        new InfixOperationNode(left.get(), getOperators().getInfixOperator(
            AndOperator.class), right.get()));
  }

  public StepWithResult and(List<StepWithResult> arguments) {
    arguments.removeIf(Objects::isNull);
    if (arguments.isEmpty()) {
      throw new IllegalArgumentException("At least one not null argument should be present");
    }
    return Utils.merge(this::and, arguments);
  }

  public StepWithResult and(StepWithResult... arguments) {
    return and(Arrays.asList(arguments));
  }

  private StepWithResult or(@Nullable StepWithResult left, @Nullable StepWithResult right) {
    if (left == null && right == null) {
      throw new IllegalArgumentException();
    }
    if (left == null) {
      return right;
    }
    if (right == null) {
      return left;
    }
    return new StepWithResultImpl(getOperators(),
        new InfixOperationNode(left.get(), getOperators().getInfixOperator(
            OrOperator.class), right.get()));
  }

  public StepWithResult or(List<StepWithResult> arguments) {
    arguments.removeIf(Objects::isNull);
    if (arguments.isEmpty()) {
      throw new IllegalArgumentException("At least one not null argument should be present");
    }
    return Utils.merge(this::or, arguments);
  }

  public StepWithResult or(StepWithResult... arguments) {
    return or(Arrays.asList(arguments));
  }

}
