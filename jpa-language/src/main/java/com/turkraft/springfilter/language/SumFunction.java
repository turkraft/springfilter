package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class SumFunction extends FilterFunction {

  public SumFunction() {
    super("sum");
  }

  @Override
  public String getDescription() {
    return "Add numbers or aggregate sum";
  }

  @Override
  public String getExample() {
    return "sum(orderItems.price) > 500";
  }

}
