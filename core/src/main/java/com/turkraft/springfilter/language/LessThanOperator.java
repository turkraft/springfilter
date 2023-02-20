package com.turkraft.springfilter.language;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.definition.FilterInfixOperator;

@Component
public class LessThanOperator extends FilterInfixOperator {

  public LessThanOperator() {
    super("<", 100);
  }

}
