package com.torshid.springfilter.node;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.query.criteria.internal.path.PluralAttributePath;

import com.torshid.springfilter.Utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionNoInput extends Condition {

  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    Path<?> path = Utils.buildDatabasePath(root, joins, getField());

    switch (getComparator()) {

      case EMPTY:
      case NULL:
        if (path instanceof PluralAttributePath) {
          return criteriaBuilder.isEmpty((PluralAttributePath) path);
        } else {
          return criteriaBuilder.isNull(path);
        }

      case NOT_EMPTY:
      case NOT_NULL:
        if (path instanceof PluralAttributePath) {
          return criteriaBuilder.isNotEmpty((PluralAttributePath) path);
        } else {
          return criteriaBuilder.isNotNull(path);
        }

      default:
        throw new RuntimeException("Unsupported condition comparator " + getComparator().getLiteral());

    }

  }

}
