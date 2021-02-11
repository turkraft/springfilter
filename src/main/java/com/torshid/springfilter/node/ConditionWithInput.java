package com.torshid.springfilter.node;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.torshid.compiler.node.INode;
import com.torshid.springfilter.Utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionWithInput extends Condition {

  private IExpression<?> input;

  @Override
  public INode transform(INode parent) {
    return this;
  }

  @Override
  public String generate() {
    return super.generate() + " " + getInput().generate();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    Path<?> path = Utils.buildDatabasePath(root, joins, getField());

    Class<?> fieldType = path.getJavaType();

    if (!getComparator().getFieldType().isAssignableFrom(fieldType)) {
      throw new UnsupportedOperationException("The comparator " + getComparator().getLiteral()
          + " only supports fields of type " + getComparator().getFieldType());
    }

    Expression<?> input = getInput().generate(root, criteriaQuery, criteriaBuilder, joins);

    switch (getComparator()) {

      case EQUAL:
        return criteriaBuilder.equal(path, input);

      case NOT_EQUAL:
        return criteriaBuilder.notEqual(path, input);

      case GREATER_THAN:
        return criteriaBuilder.greaterThan((Expression<? extends Comparable>) path,
            (Expression<? extends Comparable>) input);

      case GREATER_THAN_EQUAL:
        return criteriaBuilder.greaterThanOrEqualTo((Expression<? extends Comparable>) path,
            (Expression<? extends Comparable>) input);

      case LESS_THAN:
        return criteriaBuilder.lessThan((Expression<? extends Comparable>) path,
            (Expression<? extends Comparable>) input);

      case LESS_THAN_EQUAL:
        return criteriaBuilder.lessThanOrEqualTo((Expression) path, (Comparable) input);

      case LIKE: {
        return criteriaBuilder.like(criteriaBuilder.upper((Expression) path),
            "%" + input.toString().trim().toUpperCase() + "%");
      }

      default:
        throw new UnsupportedOperationException("Unsupported comparator " + getComparator().getLiteral());

    }

  }

}
