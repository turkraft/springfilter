package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class PowerFunction extends FilterFunction {

  public PowerFunction() {
    super("power");
  }

  @Override
  public String getDescription() {
    return "Raise to power";
  }

  @Override
  public String getExample() {
    return "power(radius, 2) > 100";
  }

}
