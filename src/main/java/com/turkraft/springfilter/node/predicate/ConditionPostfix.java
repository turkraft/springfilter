package com.turkraft.springfilter.node.predicate;

import java.util.Collection;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import com.turkraft.springfilter.exception.InvalidQueryException;
import com.turkraft.springfilter.node.IExpression;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionPostfix extends Condition {

  private IExpression left;

  @Override
  public IExpression transform(IExpression parent) {
    left = left.transform(this);
    return this;
  }

  @Override
  public String generate() {
    String generatedLeft = left.generate();
    if (generatedLeft.isEmpty())
      return "";
    return generatedLeft + " " + getComparator().getLiteral();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Predicate generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload) {

    Expression<?> leftExpression =
        getLeft().generate(root, criteriaQuery, criteriaBuilder, joins, payload);

    switch (getComparator()) {

      case EMPTY:
      case NULL:
        if (Collection.class.isAssignableFrom(leftExpression.getJavaType())
            && leftExpression instanceof PluralAttributePath) {
          return criteriaBuilder.isEmpty((Expression<Collection<?>>) leftExpression);
        } else {
          return criteriaBuilder.isNull(leftExpression);
        }

      case NOT_EMPTY:
      case NOT_NULL:
        if (Collection.class.isAssignableFrom(leftExpression.getJavaType())
            && leftExpression instanceof PluralAttributePath) {
          return criteriaBuilder.isNotEmpty((Expression<Collection<?>>) leftExpression);
        } else {
          return criteriaBuilder.isNotNull(leftExpression);
        }

      default:
        throw new InvalidQueryException(
            "The comparator " + getComparator().getLiteral() + " is unsupported");

    }

  }

}
