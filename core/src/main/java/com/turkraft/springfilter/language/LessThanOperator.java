package com.turkraft.springfilter.language;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.definition.FilterInfixOperator;

@Component
public class LessThanOperator extends FilterInfixOperator {

  public LessThanOperator() {
    super("<", 100);
  }

  @Override
  public String getDescription() {
    return "Less than";
  }

  @Override
  public String getExample() {
    return "age < 18";
  }

}
