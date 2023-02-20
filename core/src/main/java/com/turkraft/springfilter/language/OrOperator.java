package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class OrOperator extends FilterInfixOperator {

  public OrOperator() {
    super("or", 25);
  }

}
