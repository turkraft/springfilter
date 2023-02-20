package com.turkraft.springfilter.language;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.definition.FilterInfixOperator;

@Component
public class GreaterThanOrEqualOperator extends FilterInfixOperator {

  public GreaterThanOrEqualOperator() {
    super(">:", 100);
  }

}
