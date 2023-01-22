package com.turkraft.springfilter.parser.generator.bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bson.BsonNull;
import org.bson.conversions.Bson;
import org.springframework.data.annotation.Id;
import com.mongodb.client.model.Filters;
import com.turkraft.springfilter.FilterParameters;
import com.turkraft.springfilter.exception.BadFilterSyntaxException;
import com.turkraft.springfilter.exception.InternalFilterException;
import com.turkraft.springfilter.exception.UnimplementFilterOperationException;
import com.turkraft.springfilter.parser.EntityFieldTypeResolver;
import com.turkraft.springfilter.parser.Filter;
import com.turkraft.springfilter.parser.FilterBaseVisitor;
import com.turkraft.springfilter.parser.FilterParser.FieldContext;
import com.turkraft.springfilter.parser.FilterParser.FilterContext;
import com.turkraft.springfilter.parser.FilterParser.FunctionContext;
import com.turkraft.springfilter.parser.FilterParser.InfixContext;
import com.turkraft.springfilter.parser.FilterParser.InputContext;
import com.turkraft.springfilter.parser.FilterParser.PostfixContext;
import com.turkraft.springfilter.parser.FilterParser.PredicateContext;
import com.turkraft.springfilter.parser.FilterParser.PrefixContext;
import com.turkraft.springfilter.parser.FilterParser.PriorityContext;
import com.turkraft.springfilter.parser.StringConverter;
import com.turkraft.springfilter.parser.operation.InfixOperation;
import com.turkraft.springfilter.parser.operation.PostfixOperation;
import com.turkraft.springfilter.parser.operation.PrefixOperation;

public class BsonGenerator extends FilterBaseVisitor<Bson> {

  public static Bson run(Filter filter, Class<?> entityClass) {
    Objects.requireNonNull(filter);
    Objects.requireNonNull(entityClass);
    return new BsonGenerator(entityClass).visit(filter);
  }

  public static Bson run(String query, Class<?> entityClass) {
    return run(Filter.from(query), entityClass);
  }

  private final Class<?> entityClass;

  protected BsonGenerator(Class<?> entityClass) {
    this.entityClass = entityClass;
  }

  @Override
  public Bson visitFilter(FilterContext ctx) {
    if (ctx.predicate() == null) {
      return null;
    }
    return visit(ctx.predicate());
  }

  @Override
  public Bson visitPriority(PriorityContext ctx) {
    return visit(ctx.predicate());
  }

  @Override
  public Bson visitPrefix(PrefixContext ctx) {

    PrefixOperation op = PrefixOperation.from(ctx.operator.getType());

    if (!(ctx.left instanceof FieldContext)) {
      throw new BadFilterSyntaxException(
          "Left-hand side of the " + op + " operation should be a field");
    }

    String fieldName = ctx.left.getText();

    switch (op) {

      case IS_NULL:
        return Filters.eq(fieldName, BsonNull.VALUE);

      case IS_NOT_NULL:
        return Filters.ne(fieldName, BsonNull.VALUE);

      case IS_EMPTY:
        return Filters.size(fieldName, 0);

      case IS_NOT_EMPTY:
        return Filters.not(Filters.size(fieldName, 0));

    }

    throw new UnimplementFilterOperationException(op);

  }

  @Override
  public Bson visitInfix(InfixContext ctx) {

    InfixOperation op = InfixOperation.from(ctx.operator.getType());

    switch (op) {

      case AND:
        return Filters.and(visit(ctx.left), visit(ctx.right));

      case OR:
        return Filters.or(visit(ctx.left), visit(ctx.right));

      case LIKE:
      case EQUAL:
      case NOT_EQUAL:
      case GREATER_THAN:
      case GREATER_THAN_OR_EQUAL:
      case LESS_THAN:
      case LESS_THAN_OR_EQUAL:
        return visitComparison(ctx, op);

      case IN:
        return visitIn(ctx);

    }

    throw new UnimplementFilterOperationException(op);

  }

  @SuppressWarnings("incomplete-switch")
  private Bson visitComparison(InfixContext ctx, InfixOperation op) {

    if (!(ctx.left instanceof FieldContext)) {
      throw new BadFilterSyntaxException(
          "Left-hand side of the " + op + " operation should be a field");
    }

    if (!(ctx.right instanceof InputContext)) {
      throw new BadFilterSyntaxException(
          "Right-hand side of the " + op + " operation should be an input");
    }

    String fieldName = ctx.left.getText();
    Class<?> fieldClass = EntityFieldTypeResolver.resolve(fieldName, entityClass);
    Object input = StringConverter.convert(ctx.right.getText(), fieldClass);

    switch (op) {

      case LIKE:
        if (String.class.isAssignableFrom(fieldClass) && !fieldName.contains(".")) {
          java.lang.reflect.Field field = EntityFieldTypeResolver.getField(entityClass, fieldName);
          if (field.isAnnotationPresent(Id.class)) {
            return Filters.where("/" + input + "/.test(this._id)");
          }
        }
        if (FilterParameters.CASE_SENSITIVE_LIKE_OPERATOR) {
          return Filters.regex(fieldName, input.toString());
        } else {
          return Filters.regex(fieldName, input.toString(), "i");
        }

      case EQUAL:
        return Filters.eq(fieldName, input);

      case NOT_EQUAL:
        return Filters.ne(fieldName, input);

      case GREATER_THAN:
        return Filters.gt(fieldName, input);

      case GREATER_THAN_OR_EQUAL:
        return Filters.gte(fieldName, input);

      case LESS_THAN:
        return Filters.lt(fieldName, input);

      case LESS_THAN_OR_EQUAL:
        return Filters.lte(fieldName, input);

    }

    throw new UnimplementFilterOperationException(op);

  }

  private Bson visitIn(InfixContext ctx) {

    if (!(ctx.left instanceof FieldContext)) {
      throw new BadFilterSyntaxException("Left-hand side of the IN operation should be a field");
    }

    String fieldName = ctx.left.getText();
    Class<?> fieldClass = EntityFieldTypeResolver.resolve(fieldName, entityClass);

    List<Object> arguments = new ArrayList<Object>();

    for (PredicateContext argument : ctx.arguments) {

      if (!(argument instanceof InputContext)) {
        throw new BadFilterSyntaxException(
            "Arguments of the IN operation should be made of inputs");
      }

      arguments.add(StringConverter.convert(argument.getText(), fieldClass));

    }

    return Filters.in(fieldName, arguments);

  }

  @Override
  public Bson visitPostfix(PostfixContext ctx) {

    PostfixOperation op = PostfixOperation.from(ctx.operator.getType());

    switch (op) {

      case NOT:
        return Filters.not(visit(ctx.right));

    }

    throw new UnimplementFilterOperationException(op);

  }

  @Override
  public Bson visitFunction(FunctionContext ctx) {
    throw new UnimplementFilterOperationException("Functions are not supported yet");
  }

  @Override
  public Bson visitField(FieldContext ctx) {
    throw new InternalFilterException("A field can't be generated directly");
  }

  @Override
  public Bson visitInput(InputContext ctx) {
    throw new InternalFilterException("An input can't be generated directly");
  }

  public Class<?> getEntityClass() {
    return entityClass;
  }

}
