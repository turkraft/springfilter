package com.turkraft.springfilter.language;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.definition.FilterInfixOperator;

@Component
public class LessThanOrEqualOperator extends FilterInfixOperator {

  public LessThanOrEqualOperator() {
    super("<:", 100);
  }

}
