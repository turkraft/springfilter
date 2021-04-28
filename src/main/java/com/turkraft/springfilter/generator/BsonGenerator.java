package com.turkraft.springfilter.generator;

import java.util.ArrayList;
import java.util.List;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.turkraft.springfilter.compiler.node.Arguments;
import com.turkraft.springfilter.compiler.node.Field;
import com.turkraft.springfilter.compiler.node.Function;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.compiler.node.Input;
import com.turkraft.springfilter.compiler.node.predicate.ConditionInfix;
import com.turkraft.springfilter.compiler.node.predicate.ConditionPostfix;
import com.turkraft.springfilter.compiler.node.predicate.OperationInfix;
import com.turkraft.springfilter.compiler.node.predicate.OperationPrefix;
import com.turkraft.springfilter.compiler.token.Comparator;
import com.turkraft.springfilter.exception.InvalidQueryException;

public class BsonGenerator implements Generator<Bson> {

  private static final BsonGenerator INSTANCE = new BsonGenerator();

  public static Bson run(IExpression expression) {
    return INSTANCE.generate(expression);
  }

  @Override
  public Bson generate(Field expression) {
    throw new InvalidQueryException("A field can't be generated directly");
  }

  @Override
  public Bson generate(Function expression) {
    throw new InvalidQueryException("Functions are not supported");
  }

  @Override
  public Bson generate(ConditionInfix expression) {

    if (expression.getComparator() == Comparator.IN) { // TODO: 'in' should be a different node
      return inCondition(expression);
    }

    if (!(expression.getLeft() instanceof Field)) {
      throw new InvalidQueryException("Left side of the comparator "
          + expression.getComparator().getLiteral() + " should be a field");
    }

    if (!(expression.getRight() instanceof Input)) {
      throw new InvalidQueryException("Right side of the comparator "
          + expression.getComparator().getLiteral() + " should be an input");
    }

    String key = ((Field) expression.getLeft()).getName();
    Object input = ((Input) expression.getRight()).getValue().getValue();

    switch (expression.getComparator()) {

      case EQUAL:
        return com.mongodb.client.model.Filters.eq(key, input);

      case NOT_EQUAL:
        return com.mongodb.client.model.Filters.ne(key, input);

      case GREATER_THAN:
        return com.mongodb.client.model.Filters.gt(key, input);

      case GREATER_THAN_OR_EQUAL:
        return com.mongodb.client.model.Filters.gte(key, input);

      case LESS_THAN:
        return com.mongodb.client.model.Filters.lt(key, input);

      case LESS_THAN_OR_EQUAL:
        return com.mongodb.client.model.Filters.lte(key, input);

      case LIKE: {
        return com.mongodb.client.model.Filters.regex(key, input.toString());
      }

      default:
        throw new InvalidQueryException(
            "Unsupported comparator " + expression.getComparator().getLiteral());

    }

  }

  private Bson inCondition(ConditionInfix expression) {

    if (!(expression.getLeft() instanceof Field)) {
      throw new InvalidQueryException("Left expression of the comparator "
          + expression.getComparator().getLiteral() + " should be a field");
    }

    if (!(expression.getRight() instanceof Arguments)) {
      throw new InvalidQueryException("Right expression of the comparator "
          + expression.getComparator().getLiteral() + " should be arguments");
    }

    List<Object> arguments = new ArrayList<Object>();

    for (IExpression argument : ((Arguments) expression.getRight()).getValues()) {

      if (!(argument instanceof Input)) {
        throw new InvalidQueryException("Right expression of the comparator "
            + expression.getComparator().getLiteral() + " should be made of only input arguments");
      }

      arguments.add(((Input) argument).getValue().getValue());

    }

    return com.mongodb.client.model.Filters.in(((Field) expression.getLeft()).getName(), arguments);

  }

  @Override
  public Bson generate(ConditionPostfix expression) {

    // TODO: implement

    switch (expression.getComparator()) {

      default:
        throw new InvalidQueryException(
            "The comparator " + expression.getComparator().getLiteral() + " is unsupported");

    }

  }

  @Override
  public Bson generate(OperationInfix expression) {

    switch (expression.getOperator()) {

      case AND:
        return Filters.and(generate(expression.getLeft()), generate(expression.getRight()));

      case OR:
        return Filters.or(generate(expression.getLeft()), generate(expression.getRight()));

      default:
        throw new InvalidQueryException(
            "Unsupported infix operator " + expression.getOperator().getLiteral());

    }

  }

  @Override
  public Bson generate(OperationPrefix expression) {

    switch (expression.getOperator()) {

      case NOT:
        return Filters.not(generate(expression.getRight()));

      default:
        throw new InvalidQueryException(
            "Unsupported prefix operator " + expression.getOperator().getLiteral());

    }

  }

  @Override
  public Bson generate(Input expression, Class<?> targetClass) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

}
