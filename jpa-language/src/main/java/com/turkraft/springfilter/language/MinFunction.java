package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class MinFunction extends FilterFunction {

  public MinFunction() {
    super("min");
  }

  @Override
  public String getDescription() {
    return "Minimum collection value";
  }

  @Override
  public String getExample() {
    return "min(orderItems.price) < 10";
  }

}
