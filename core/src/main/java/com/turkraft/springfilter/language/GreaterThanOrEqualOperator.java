package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class GreaterThanOrEqualOperator extends FilterInfixOperator {

  public GreaterThanOrEqualOperator() {
    super(new String[]{">:", ">="}, 100);
  }

  @Override
  public String getDescription() {
    return "Greater than or equal";
  }

  @Override
  public String getExample() {
    return "age >= 18";
  }

}
