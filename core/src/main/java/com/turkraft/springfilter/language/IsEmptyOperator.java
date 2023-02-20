package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterPostfixOperator;
import org.springframework.stereotype.Component;

@Component
public class IsEmptyOperator extends FilterPostfixOperator {

  public IsEmptyOperator() {
    super("is empty", 100);
  }

}
