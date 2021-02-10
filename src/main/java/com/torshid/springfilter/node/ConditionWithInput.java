package com.torshid.springfilter.node;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.torshid.compiler.node.Node;
import com.torshid.springfilter.Utils;
import com.torshid.springfilter.token.input.Input;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionWithInput extends Condition {

  private Input<?> input;

  @Override
  public Node transform(Node parent) {
    return this;
  }

  @Override
  public String generate() {
    return super.generate() + " '" + getInput().getValue() + "'";
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

    Object castedInput = getInput().getValueAs(fieldType);

    switch (getComparator()) {

      case EQUAL:
        return criteriaBuilder.equal(path, castedInput);

      case NOT_EQUAL:
        return criteriaBuilder.notEqual(path, castedInput);

      case GREATER_THAN:
        return criteriaBuilder.greaterThan((Expression) path, (Comparable) castedInput);

      case GREATER_THAN_EQUAL:
        return criteriaBuilder.greaterThanOrEqualTo((Expression) path, (Comparable) castedInput);

      case LESS_THAN:
        return criteriaBuilder.lessThan((Expression) path, (Comparable) castedInput);

      case LESS_THAN_EQUAL:
        return criteriaBuilder.lessThanOrEqualTo((Expression) path, (Comparable) castedInput);

      case LIKE: {
        return criteriaBuilder.like(criteriaBuilder.upper((Expression) path),
            "%" + castedInput.toString().trim().toUpperCase() + "%");
      }

      default:
        throw new UnsupportedOperationException("Unsupported comparator " + getComparator().getLiteral());

    }

  }

}
