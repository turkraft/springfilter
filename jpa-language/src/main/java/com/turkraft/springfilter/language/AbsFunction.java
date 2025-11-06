package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class AbsFunction extends FilterFunction {

  public AbsFunction() {
    super("abs");
  }

  @Override
  public String getDescription() {
    return "Absolute value";
  }

  @Override
  public String getExample() {
    return "abs(balance) > 100";
  }

}
