package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class SqrtFunction extends FilterFunction {

  public SqrtFunction() {
    super("sqrt");
  }

  @Override
  public String getDescription() {
    return "Square root";
  }

  @Override
  public String getExample() {
    return "sqrt(area) > 10";
  }

}
