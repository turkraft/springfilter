package com.turkraft.springfilter.node;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import com.turkraft.springfilter.Pair;
import com.turkraft.springfilter.exception.InvalidQueryException;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Function implements IExpression {

  private String name;

  private Arguments arguments;

  @Override
  public Function transform(IExpression parent) {
    arguments = arguments.transform(this);
    return this;
  }

  @Override
  public String generate() {
    if (name.isEmpty()) {
      return "";
    }
    return name + arguments.generate();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Expression<?> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload) {

    List<Pair<IExpression, Expression<?>>> expressions = new LinkedList<>();

    // the generated expression is directly stored in the list if it's not based on an input,
    // otherwise we will generate it later

    for (IExpression argument : arguments.getValues()) {
      if (argument instanceof Input) {
        expressions.add(new Pair(argument, null));
      } else {
        expressions.add(new Pair(argument,
            argument.generate(root, criteriaQuery, criteriaBuilder, joins, payload)));
      }
    }

    Type type = Type.getMatch(name, expressions);

    if (type == null) {
      throw new InvalidQueryException("The function " + name + " didn't have any match");
    }

    // the following method is used to get the expression of the argument at the given index

    java.util.function.Function<Integer, Expression<?>> getter = (index) -> {
      if (expressions.get(index).getKey() instanceof Input) {
        return ((Input) expressions.get(index).getKey()).generate(root, criteriaQuery,
            criteriaBuilder, joins, type.argumentTypes[index]);
      }
      return expressions.get(index).getValue();
    };

    switch (type) {

      case ABSOLUTE:
        return criteriaBuilder.abs((Expression<Number>) getter.apply(0));

      case AVERAGE:
        return criteriaBuilder.avg((Expression<Number>) getter.apply(0));

      case MIN:
        return criteriaBuilder.min((Expression<Number>) getter.apply(0));

      case MAX:
        return criteriaBuilder.max((Expression<Number>) getter.apply(0));

      case SUM:
        return criteriaBuilder.sum((Expression<Number>) getter.apply(0));

      case CURRENTDATE:
        return criteriaBuilder.currentDate();

      case CURRENTTIME:
        return criteriaBuilder.currentTime();

      case CURRENTTIMESTAMP:
        return criteriaBuilder.currentTimestamp();

      case SIZE:
        return criteriaBuilder.size((Expression<Collection>) getter.apply(0));

      case LENGTH:
        return criteriaBuilder.length((Expression<String>) getter.apply(0));

      case TRIM:
        return criteriaBuilder.trim((Expression<String>) getter.apply(0));

    }

    throw new InvalidQueryException("Unsupported function " + type.name().toLowerCase());

  }

  public enum Type {

    ABSOLUTE(Number.class), AVERAGE(Number.class), MIN(Number.class), MAX(Number.class), SUM(
        Number.class), SIZE(Collection.class), LENGTH(
            String.class), TRIM(String.class), CURRENTTIME, CURRENTDATE, CURRENTTIMESTAMP;

    private final Class<?>[] argumentTypes;

    Type(Class<?>... argumentTypes) {
      this.argumentTypes = argumentTypes;
    }

    public Class<?>[] getArgumentTypes() {
      return argumentTypes;
    }

    public boolean matches(String name, List<Pair<IExpression, Expression<?>>> expressions) {

      if (!name().equalsIgnoreCase(name)) {
        return false;
      }

      if (argumentTypes.length > expressions.size()) {
        throw new InvalidQueryException(
            "The function " + name + " should have " + argumentTypes.length + " arguments");
      }

      for (int i = 0; i < argumentTypes.length; i++) {

        if (expressions.get(i).getKey() instanceof Input) {
          if (!(((Input) expressions.get(i).getKey()).getValue().canBe(argumentTypes[i]))) {
            return false;
          }
        } else if (!argumentTypes[i]
            .isAssignableFrom(expressions.get(i).getValue().getJavaType())) {
          return false;
        }

      }

      return true;

    }

    public static Type getMatch(String name, List<Pair<IExpression, Expression<?>>> expressions) {

      for (Type type : values()) {
        if (type.matches(name, expressions)) {
          return type;
        }
      }

      return null;

    }

  }

}
