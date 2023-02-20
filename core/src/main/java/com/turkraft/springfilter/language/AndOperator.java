package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class AndOperator extends FilterInfixOperator {

  public AndOperator() {
    super("and", 50);
  }

}
