package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ExistsFunction extends FilterFunction {

  public ExistsFunction() {
    super("exists");
  }

  @Override
  public String getDescription() {
    return "Subquery filter";
  }

  @Override
  public String getExample() {
    return "exists(orders.total > 100 and orders.status : 'shipped')";
  }

}
