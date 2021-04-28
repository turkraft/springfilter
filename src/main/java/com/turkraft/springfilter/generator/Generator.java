package com.turkraft.springfilter.generator;

import com.turkraft.springfilter.compiler.node.Arguments;
import com.turkraft.springfilter.compiler.node.Field;
import com.turkraft.springfilter.compiler.node.Filter;
import com.turkraft.springfilter.compiler.node.Function;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.compiler.node.Input;
import com.turkraft.springfilter.compiler.node.Nothing;
import com.turkraft.springfilter.compiler.node.predicate.Condition;
import com.turkraft.springfilter.compiler.node.predicate.ConditionInfix;
import com.turkraft.springfilter.compiler.node.predicate.ConditionPostfix;
import com.turkraft.springfilter.compiler.node.predicate.Operation;
import com.turkraft.springfilter.compiler.node.predicate.OperationInfix;
import com.turkraft.springfilter.compiler.node.predicate.OperationPrefix;
import com.turkraft.springfilter.compiler.node.predicate.Priority;
import com.turkraft.springfilter.exception.InvalidQueryException;

public interface Generator<T> {

  default T generate(IExpression expression) {

    if (expression instanceof Filter) {
      return generate((Filter) expression);
    }

    if (expression instanceof Arguments) {
      return generate((Arguments) expression);
    }

    if (expression instanceof Field) {
      return generate((Field) expression);
    }

    if (expression instanceof Function) {
      return generate((Function) expression);
    }

    if (expression instanceof Condition) {
      return generate((Condition) expression);
    }

    if (expression instanceof ConditionInfix) {
      return generate((ConditionInfix) expression);
    }

    if (expression instanceof ConditionPostfix) {
      return generate((ConditionPostfix) expression);
    }

    if (expression instanceof Operation) {
      return generate((Operation) expression);
    }

    if (expression instanceof OperationInfix) {
      return generate((OperationInfix) expression);
    }

    if (expression instanceof OperationPrefix) {
      return generate((OperationPrefix) expression);
    }

    if (expression instanceof Priority) {
      return generate((Priority) expression);
    }

    if (expression instanceof Nothing) {
      return generate((Nothing) expression);
    }

    if (expression instanceof Input) {
      return generate((Input) expression, null);
    }

    throw new InvalidQueryException(
        "Expression " + expression + " is unsupported by the compiler " + this);

  }

  default T generate(Filter expression) {
    return generate((expression).getBody());
  }

  T generate(Field expression);

  T generate(Function expression);

  default T generate(Arguments expression) {
    throw new InvalidQueryException("Arguments can't be generated directly");
  }

  default T generate(Condition expression) {
    if (expression instanceof ConditionInfix)
      return generate((ConditionInfix) expression);
    return generate((ConditionPostfix) expression);
  }

  T generate(ConditionInfix expression);

  T generate(ConditionPostfix expression);

  default T generate(Operation expression) {
    if (expression instanceof OperationInfix)
      return generate((OperationInfix) expression);
    return generate((OperationPrefix) expression);
  }

  T generate(OperationInfix expression);

  T generate(OperationPrefix expression);

  default T generate(Priority expression) {
    return generate((expression).getBody());
  }

  default T generate(Nothing expression) {
    return null;
  }

  T generate(Input expression, Class<?> targetClass);

}
