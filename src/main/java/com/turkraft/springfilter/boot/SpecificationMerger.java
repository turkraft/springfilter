package com.turkraft.springfilter.boot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import com.turkraft.springfilter.compiler.node.Nothing;

public class SpecificationMerger<T> implements Specification<T> {

  private static final long serialVersionUID = 1L;

  private List<Specification<T>> specifications;

  @Override
  public Predicate toPredicate(
      Root<T> root,
      CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {

    Map<String, Join<?, ?>> joins = new HashMap<String, Join<?, ?>>();

    Specification<T> result = null;

    for (Specification<T> specification : specifications) {

      if (specification == null) {
        continue;
      }

      if (specification instanceof FilterSpecification) {

        FilterSpecification<T> filterSpecification = (FilterSpecification<T>) specification;

        if ((filterSpecification.getInput() == null
            || filterSpecification.getInput().trim().isEmpty())
            && (filterSpecification.getFilter() == null
                || filterSpecification.getFilter() instanceof Nothing)) {
          continue;
        }

        filterSpecification.setJoins(joins);

      }

      result = result == null ? specification : specification.and(result);

    }

    if (result == null) {
      return null;
    }

    return result.toPredicate(root, query, criteriaBuilder);

  }

  public List<Specification<T>> getSpecifications() {
    if (specifications == null) {
      specifications = new LinkedList<Specification<T>>();
    }
    return specifications;
  }

  public void setSpecifications(List<Specification<T>> specifications) {
    this.specifications = specifications;
  }

}
