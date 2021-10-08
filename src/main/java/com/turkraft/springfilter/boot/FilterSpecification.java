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
import com.turkraft.springfilter.parser.Filter;
import com.turkraft.springfilter.parser.generator.expression.ExpressionGenerator;

/**
 * The filter's {@link org.springframework.data.jpa.domain.Specification Specification&lt;T&gt;}.
 */

public class FilterSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1L;

  private final String input;

  private final Filter filter;

  private Object payload;

  // should be set when common join set with other specifications is required
  private Map<String, Join<?, ?>> joins;

  public FilterSpecification(String input) {
    Objects.requireNonNull(input);
    this.input = input;
    this.filter = null;
  }

  public FilterSpecification(Filter filter) {
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

    Map<String, Join<?, ?>> j = joins != null ? joins : new HashMap<String, Join<?, ?>>();

    if (input != null) {
      predicate =
          !input.trim().isEmpty()
              ? (Predicate) ExpressionGenerator.run(Filter.from(input), root, query,
                  criteriaBuilder, j, payload)
              : null;
    } else {
      predicate =
          (Predicate) ExpressionGenerator.run(filter, root, query, criteriaBuilder, j, payload);
    }

    return predicate;

  }

  public String getInput() {
    return input;
  }

  public Filter getFilter() {
    return filter;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

  public Map<String, Join<?, ?>> getJoins() {
    return joins;
  }

  public void setJoins(Map<String, Join<?, ?>> joins) {
    this.joins = joins;
  }

  @SafeVarargs
  public static <T> Specification<T> merge(Specification<T>... specifications) {

    SpecificationMerger<T> merger = new SpecificationMerger<T>();

    for (Specification<T> specification : specifications) {

      if (specification == null) {
        continue;
      }

      if (specification instanceof SpecificationMerger) {
        merger.getSpecifications()
            .addAll(((SpecificationMerger<T>) specification).getSpecifications());
      } else {
        merger.getSpecifications().add(specification);
      }
    }

    return merger;

  }

}
