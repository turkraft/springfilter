package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class MaxFunction extends FilterFunction {

  public MaxFunction() {
    super("max");
  }

  @Override
  public String getDescription() {
    return "Maximum collection value";
  }

  @Override
  public String getExample() {
    return "max(orderItems.price) > 100";
  }

}
