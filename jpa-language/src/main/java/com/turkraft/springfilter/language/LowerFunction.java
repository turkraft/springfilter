package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class LowerFunction extends FilterFunction {

  public LowerFunction() {
    super("lower");
  }

  @Override
  public String getDescription() {
    return "Convert to lowercase";
  }

  @Override
  public String getExample() {
    return "lower(name) ~ 'john%'";
  }

}
