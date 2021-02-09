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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionWithInput extends Condition {

  private Object input;

  @Override
  public Node transform(Node parent) {
    return this;
  }

  @Override
  public String generate() {
    return super.generate() + input;
  }

  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    Path<?> path = Utils.buildDatabasePath(root, joins, getField());

    switch (getComparator()) {

      case EQUAL:
        return criteriaBuilder.equal(path, getInput());

      case NOT_EQUAL:
        return criteriaBuilder.notEqual(path, getInput());

      case GREATER_THAN:
        return criteriaBuilder.greaterThan((Expression) path, (Comparable) getInput());

      case GREATER_THAN_EQUAL:
        return criteriaBuilder.greaterThanOrEqualTo((Expression) path, (Comparable) getInput());

      case LESS_THAN:
        return criteriaBuilder.lessThan((Expression) path, (Comparable) getInput());

      case LESS_THAN_EQUAL:
        return criteriaBuilder.lessThanOrEqualTo((Expression) path, (Comparable) getInput());

      case LIKE: {
        return criteriaBuilder.like(criteriaBuilder.upper((Expression) path),
            "%" + getInput().toString().trim().toUpperCase() + "%");
      }

      default:
        throw new RuntimeException("Unsupported condition comparator " + getComparator().getLiteral());

    }

  }

}
