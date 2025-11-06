package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class OrOperator extends FilterInfixOperator {

  public OrOperator() {
    super("or", 25);
  }

  @Override
  public String getDescription() {
    return "Either condition must be true";
  }

  @Override
  public String getExample() {
    return "status : 'active' or status : 'pending'";
  }

}
