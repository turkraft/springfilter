package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class LnFunction extends FilterFunction {

  public LnFunction() {
    super("ln");
  }

  @Override
  public String getDescription() {
    return "Natural logarithm";
  }

  @Override
  public String getExample() {
    return "ln(value) > 2";
  }

}
