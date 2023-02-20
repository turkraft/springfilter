package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.data.jpa.domain.Specification;

public interface FilterSpecification<T> extends Specification<T> {

  FilterNode getFilter();

}
