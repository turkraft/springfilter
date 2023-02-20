package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;

public interface Step {

  FilterOperators getOperators();

  FilterStringConverter getFilterStringConverter();

}
