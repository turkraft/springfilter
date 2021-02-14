package com.turkraft.springfilter.node;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import com.turkraft.springfilter.exception.UnsupportedOperationException;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Function implements IExpression {

  private String name;

  private List<IExpression> arguments;

  @Override
  public IExpression transform(IExpression parent) {
    for (int i = 0; i < arguments.size(); i++) {
      arguments.add(arguments.remove(i).transform(this));
    }
    return this;
  }

  @Override
  public String generate() {
    return name + "(" + String.join(", ", arguments.stream().map(a -> a.generate()).collect(Collectors.toList())) + ")";
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Expression<?> generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {


    SortedMap<IExpression, Map<Class<?>, Expression<?>>> cache = new TreeMap<>();

    for (IExpression argument : arguments) {
      cache.put(argument, new HashMap<>());
    }

    List<Expression<?>> expressions = arguments.stream()
        .map(a -> a.generate(root, criteriaQuery, criteriaBuilder, joins)).collect(Collectors.toList());

    Type type = Type.getMatch(name, expressions);

    if (type == null) {
      throw new UnsupportedOperationException("The function " + name + " didn't have any match");
    }

    switch (type) {

      case SIZE:
        return criteriaBuilder.size((Expression<Collection>) expressions.get(0));


    }

    return null;

  }

  enum Type {

    SIZE(Collection.class);

    private final Class<?>[] argumentTypes;

    Type(Class<?>... argumentTypes) {
      this.argumentTypes = argumentTypes;
    }

    public Class<?>[] getArgumentTypes() {
      return argumentTypes;
    }

    public boolean matches(String name, List<Expression<?>> expressions) {

      if (!name().equalsIgnoreCase(name)) {
        return false;
      }

      if (argumentTypes.length > expressions.size()) {
        throw new UnsupportedOperationException(
            "The function " + name + " should have " + argumentTypes.length + " arguments");
      }

      for (int i = 0; i < argumentTypes.length; i++) {
        if (!argumentTypes[i].isAssignableFrom(expressions.get(i).getJavaType())) {
          return false;
        }
      }

      return true;

    }

    public static Type getMatch(String name, List<Expression<?>> expressions) {

      for (Type type : values()) {
        if (type.matches(name, expressions)) {
          return type;
        }
      }

      throw new UnsupportedOperationException("The function " + name + " didn't have any match");

    }

  }

}
