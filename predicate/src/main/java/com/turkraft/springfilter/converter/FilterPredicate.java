package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.function.Predicate;

public interface FilterPredicate<T> extends Predicate<T> {

  FilterNode getFilter();

}
