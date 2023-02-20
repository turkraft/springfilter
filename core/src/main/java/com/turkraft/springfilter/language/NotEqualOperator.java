package com.turkraft.springfilter.language;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.definition.FilterInfixOperator;

@Component
public class NotEqualOperator extends FilterInfixOperator {

  public NotEqualOperator() {
    super("!", 100);
  }

}
