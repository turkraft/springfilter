package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class AndOperator extends FilterInfixOperator {

  public AndOperator() {
    super("and", 50);
  }

  @Override
  public String getDescription() {
    return "Both conditions must be true";
  }

  @Override
  public String getExample() {
    return "status : 'active' and age > 18";
  }

}
