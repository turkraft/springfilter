package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterPostfixOperator;
import org.springframework.stereotype.Component;

@Component
public class IsNotNullOperator extends FilterPostfixOperator {

  public IsNotNullOperator() {
    super("is not null", 100);
  }

}
