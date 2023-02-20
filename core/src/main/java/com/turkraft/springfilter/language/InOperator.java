package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class InOperator extends FilterInfixOperator {

  public InOperator() {
    super("in", 100);
  }

}
