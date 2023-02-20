package com.turkraft.springfilter.language;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.definition.FilterPostfixOperator;

@Component
public class IsNotEmptyOperator extends FilterPostfixOperator {

  public IsNotEmptyOperator() {
    super("is not empty", 100);
  }

}
