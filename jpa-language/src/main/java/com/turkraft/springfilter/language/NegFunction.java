package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class NegFunction extends FilterFunction {

  public NegFunction() {
    super("neg");
  }

  @Override
  public String getDescription() {
    return "Negate a number";
  }

  @Override
  public String getExample() {
    return "neg(value) < -100";
  }

}
