package com.turkraft.springfilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import com.turkraft.springfilter.node.IExpression;

public class FilterSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1L;

  private final String filterInput;
  private final String orderInput;

  private final IExpression filter;

  public FilterSpecification(String filterInput, String orderInput) {
    Objects.requireNonNull(filterInput);
    this.filterInput = filterInput;
    this.orderInput = orderInput;
    this.filter = null;
  }

  public FilterSpecification(String filterInput) {
    Objects.requireNonNull(filterInput);
    this.filterInput = filterInput;
    this.orderInput = null;
    this.filter = null;
  }

  public FilterSpecification(IExpression filter) {
    Objects.requireNonNull(filter);
    this.filter = filter;
    this.filterInput = null;
    this.orderInput = null;
  }

  @Override
  public Predicate toPredicate(
      Root<T> root,
      CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {

    if (filterInput != null) {

      Map<String, Join<?, ?>> joins = new HashMap<String, Join<?, ?>>();

      Predicate predicate = !filterInput.trim().isEmpty()
          ? FilterParser.parse(filterInput).generate(root, query, criteriaBuilder, joins)
          : null;

      if (orderInput != null) {
        query.orderBy(FilterUtils.generateOrderBys(root, criteriaBuilder, joins, orderInput));
      }

      return predicate;

    }

    return (Predicate) filter.generate(root, query, criteriaBuilder, new HashMap<>());
  }

  public String getFilterInput() {
    return filterInput;
  }

}
