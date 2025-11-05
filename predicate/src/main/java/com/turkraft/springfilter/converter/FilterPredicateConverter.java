package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.core.convert.converter.GenericConverter;

public interface FilterPredicateConverter extends GenericConverter {

  <T> FilterPredicate<T> convert(String query, Class<T> entityType);

  <T> FilterPredicate<T> convert(FilterNode node, Class<T> entityType);

  String convert(FilterPredicate<?> predicate);

}
