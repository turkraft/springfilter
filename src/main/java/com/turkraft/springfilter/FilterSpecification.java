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

  private final String input;

  private final IExpression filter;

  private Object payload;

  public FilterSpecification(String input) {
    Objects.requireNonNull(input);
    this.input = input;
    this.filter = null;
  }

  public FilterSpecification(IExpression filter) {
    Objects.requireNonNull(filter);
    this.filter = filter;
    this.input = null;
  }

  @Override
  public Predicate toPredicate(
      Root<T> root,
      CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {

    Predicate predicate = null;

    Map<String, Join<?, ?>> joins = new HashMap<String, Join<?, ?>>();

    if (input != null) {
      predicate = !input.trim().isEmpty()
          ? FilterParser.parse(input).generate(root, query, criteriaBuilder, joins, payload)
          : null;
    } else {
      predicate = (Predicate) filter.generate(root, query, criteriaBuilder, payload);
    }

    return predicate;

  }

  public String getInput() {
    return input;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

}
