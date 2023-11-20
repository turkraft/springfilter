package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class EqualOperator extends FilterInfixOperator {

  public EqualOperator() {
    super(new String[]{":", "="}, 100);
  }

}
