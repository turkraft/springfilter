package com.springfilter.node.predicate;

import java.util.Collection;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.query.criteria.internal.path.PluralAttributePath;

import com.springfilter.node.IExpression;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionPostfix extends Condition {

  private IExpression<?> left;

  @Override
  public String generate() {
    return left.generate() + " " + getComparator().getLiteral();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    Expression<?> left = getLeft().generate(root, criteriaQuery, criteriaBuilder, joins);

    switch (getComparator()) {

      case EMPTY:
      case NULL:
        if (left instanceof PluralAttributePath) {
          return criteriaBuilder.isEmpty((Expression<Collection<?>>) left);
        } else {
          return criteriaBuilder.isNull(left);
        }

      case NOT_EMPTY:
      case NOT_NULL:
        if (left instanceof PluralAttributePath) {
          return criteriaBuilder.isNotEmpty((Expression<Collection<?>>) left);
        } else {
          return criteriaBuilder.isNotNull(left);
        }

      default:
        throw new UnsupportedOperationException("The comparator " + getComparator().getLiteral() + " is unsupported");

    }

  }

}
