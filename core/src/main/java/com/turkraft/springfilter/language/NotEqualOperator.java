package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class NotEqualOperator extends FilterInfixOperator {

  public NotEqualOperator() {
    super(new String[]{"!", "<>"}, 100);
  }

  @Override
  public String getDescription() {
    return "Values are not equal";
  }

  @Override
  public String getExample() {
    return "status ! 'deleted'";
  }

}
