package com.turkraft.springfilter.generator;

import java.util.stream.Collectors;
import com.turkraft.springfilter.compiler.node.Arguments;
import com.turkraft.springfilter.compiler.node.Field;
import com.turkraft.springfilter.compiler.node.Function;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.compiler.node.Input;
import com.turkraft.springfilter.compiler.node.Nothing;
import com.turkraft.springfilter.compiler.node.predicate.ConditionInfix;
import com.turkraft.springfilter.compiler.node.predicate.ConditionPostfix;
import com.turkraft.springfilter.compiler.node.predicate.OperationInfix;
import com.turkraft.springfilter.compiler.node.predicate.OperationPrefix;
import com.turkraft.springfilter.compiler.node.predicate.Priority;
import com.turkraft.springfilter.compiler.token.Comparator;

public class QueryGenerator implements Generator<String> {

  private static final QueryGenerator INSTANCE = new QueryGenerator();

  public static String run(IExpression expression) {
    return INSTANCE.generate(expression);
  }

  @Override
  public String generate(Field expression) {
    return expression.getName();
  }

  @Override
  public String generate(Function expression) {
    if (expression.getName().isEmpty()) {
      return "";
    }
    return expression.getName() + generate(expression.getArguments());
  }

  @Override
  public String generate(Arguments expression) {
    if (expression.getValues() == null) {
      return "()";
    }
    return "("
        + String.join(", ",
            expression.getValues().stream().map(a -> generate(a)).collect(Collectors.toList()))
        + ")";
  }

  @Override
  public String generate(ConditionInfix expression) {

    String generatedLeft = generate(expression.getLeft());
    String generatedRight = generate(expression.getRight());

    if (generatedLeft.isEmpty() || generatedRight.isEmpty())
      return "";

    if (expression.getComparator() == Comparator.IN && generatedRight.equals("()")) {
      // if right expression represent arguments with no argument
      return "";
    }

    return generatedLeft + " " + expression.getComparator().getLiteral() + " " + generatedRight;

  }

  @Override
  public String generate(ConditionPostfix expression) {
    String generatedLeft = generate(expression.getLeft());
    if (generatedLeft.isEmpty())
      return "";
    return generatedLeft + " " + expression.getComparator().getLiteral();
  }

  @Override
  public String generate(OperationInfix expression) {

    String generatedLeft = generate(expression.getLeft());
    String generatedRight = generate(expression.getRight());

    if (generatedLeft.isEmpty() || generatedRight.isEmpty()) {
      // if op = and, return "" maybe?
      if (generatedLeft.isEmpty())
        return generatedRight;
      else if (generatedRight.isEmpty())
        return generatedLeft;
      else
        return "";
    }

    return "(" + generatedLeft + " " + expression.getOperator().getLiteral() + " " + generatedRight
        + ")";

  }

  @Override
  public String generate(OperationPrefix expression) {
    String generatedRight = generate(expression.getRight());
    if (generatedRight.isEmpty())
      return "";
    return expression.getOperator().getLiteral() + "(" + generatedRight + ")";
  }

  @Override
  public String generate(Priority expression) {
    String generatedBody = generate(expression.getBody());
    if (generatedBody.isEmpty())
      return "";
    return "(" + generatedBody + ")";
  }

  @Override
  public String generate(Nothing expression) {
    return "";
  }

  @Override
  public String generate(Input expression, Class<?> targetClass) {
    if (expression.getValue() == null) {
      return "";
    }
    String generatedValue = expression.getValue().generate();
    if (generatedValue == null || generatedValue.isEmpty()) {
      return "";
    }
    return generatedValue;
  }

}
