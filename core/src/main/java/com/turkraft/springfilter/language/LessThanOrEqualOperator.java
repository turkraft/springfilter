package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class LessThanOrEqualOperator extends FilterInfixOperator {

  public LessThanOrEqualOperator() {
    super(new String[]{"<:", "<="}, 100);
  }

}
