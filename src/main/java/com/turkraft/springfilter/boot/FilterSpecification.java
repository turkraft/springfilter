package com.turkraft.springfilter.boot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import com.turkraft.springfilter.compiler.Parser;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.generator.ExpressionGenerator;

/**
 * The filter's {@link org.springframework.data.jpa.domain.Specification Specification&lt;T&gt;}.
 */

public class FilterSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1L;

  private final String input;

  private final IExpression filter;

  private Object payload;

  private Map<String, Join<?, ?>> joins;

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

    if (input != null) {
      predicate = !input.trim().isEmpty()
          ? (Predicate) ExpressionGenerator.run(Parser.parse(input), root, query, criteriaBuilder,
              getJoins(), payload)
          : null;
    } else {
      predicate = (Predicate) ExpressionGenerator.run(filter, root, query, criteriaBuilder,
          getJoins(), payload);
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

  public Map<String, Join<?, ?>> getJoins() {
    if (joins == null) {
      joins = new HashMap<String, Join<?, ?>>();
    }
    return joins;
  }

  public void setJoins(Map<String, Join<?, ?>> joins) {
    this.joins = joins;
  }

}
