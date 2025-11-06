package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class LengthFunction extends FilterFunction {

  public LengthFunction() {
    super("length");
  }

  @Override
  public String getDescription() {
    return "String character count";
  }

  @Override
  public String getExample() {
    return "length(name) > 5";
  }

}
