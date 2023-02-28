package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.definition.FilterOperators;
import org.springframework.stereotype.Service;

@Service
public class FilterBuilder extends RootStep {

  public FilterBuilder(FilterOperators operators) {
    super(operators);
  }

}
