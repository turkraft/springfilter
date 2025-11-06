package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class UpperFunction extends FilterFunction {

  public UpperFunction() {
    super("upper");
  }

  @Override
  public String getDescription() {
    return "Convert to uppercase";
  }

  @Override
  public String getExample() {
    return "upper(name) ~ 'JOHN%'";
  }

}
