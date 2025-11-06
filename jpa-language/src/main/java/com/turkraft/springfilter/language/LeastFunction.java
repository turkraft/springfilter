package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class LeastFunction extends FilterFunction {

  public LeastFunction() {
    super("least");
  }

  @Override
  public String getDescription() {
    return "Minimum value in subquery";
  }

  @Override
  public String getExample() {
    return "least(price) < 10";
  }

}
