package com.torshid.springfilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class FilterSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1L;

  private final String input;

  public FilterSpecification(String input) {
    this.input = input;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    return FilterCompiler.compile(input, root, query, criteriaBuilder);
  }

  public String getInput() {
    return input;
  }

}
