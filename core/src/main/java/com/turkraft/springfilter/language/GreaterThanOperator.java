package com.turkraft.springfilter.language;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.definition.FilterInfixOperator;

@Component
public class GreaterThanOperator extends FilterInfixOperator {

  public GreaterThanOperator() {
    super(">", 100);
  }

  @Override
  public String getDescription() {
    return "Greater than";
  }

  @Override
  public String getExample() {
    return "age > 18";
  }

}
