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

  private Object payload;

  public FilterSpecification(String filterInput, String orderInput) {
    Objects.requireNonNull(filterInput);
    this.filterInput = filterInput;
    this.orderInput = orderInput;
    this.filter = null;
  }

  public FilterSpecification(String filterInput) {
    this(filterInput, null);
  }

  public FilterSpecification(IExpression filter, String orderInput) {
    Objects.requireNonNull(filter);
    this.filter = filter;
    this.filterInput = null;
    this.orderInput = orderInput;
  }

  public FilterSpecification(IExpression filter) {
    this(filter, null);
  }

  @Override
  public Predicate toPredicate(
      Root<T> root,
      CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {

    Predicate predicate = null;

    Map<String, Join<?, ?>> joins = new HashMap<String, Join<?, ?>>();

    if (filterInput != null) {
      predicate = !filterInput.trim().isEmpty()
          ? FilterParser.parse(filterInput).generate(root, query, criteriaBuilder, joins, payload)
          : null;
    } else {
      predicate = (Predicate) filter.generate(root, query, criteriaBuilder, payload);
    }

    if (orderInput != null) {
      query
          .orderBy(FilterUtils.generateOrderBys(root, criteriaBuilder, joins, payload, orderInput));
    }

    return predicate;

  }

  public String getFilterInput() {
    return filterInput;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

}
