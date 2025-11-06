package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class AvgFunction extends FilterFunction {

  public AvgFunction() {
    super("avg");
  }

  @Override
  public String getDescription() {
    return "Average of collection values";
  }

  @Override
  public String getExample() {
    return "avg(orderItems.price) > 50";
  }

}
