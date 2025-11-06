package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ExpFunction extends FilterFunction {

  public ExpFunction() {
    super("exp");
  }

  @Override
  public String getDescription() {
    return "Exponential (e^x)";
  }

  @Override
  public String getExample() {
    return "exp(rate) > 10";
  }

}
