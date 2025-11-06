package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class InOperator extends FilterInfixOperator {

  public InOperator() {
    super("in", 100);
  }

  @Override
  public String getDescription() {
    return "Value in list";
  }

  @Override
  public String getExample() {
    return "status in ('active', 'pending')";
  }

}
