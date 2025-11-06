package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class CountDistinctFunction extends FilterFunction {

  public CountDistinctFunction() {
    super("countDistinct");
  }

  @Override
  public String getDescription() {
    return "Count unique values (not implemented)";
  }

  @Override
  public String getExample() {
    return "countDistinct(orders.customerId) > 5";
  }

}
