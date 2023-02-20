package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class NotInOperator extends FilterInfixOperator {

  public NotInOperator() {
    super("not in", 100);
  }

}
