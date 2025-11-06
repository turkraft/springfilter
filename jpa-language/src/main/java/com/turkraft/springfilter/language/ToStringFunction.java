package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ToStringFunction extends FilterFunction {

  public ToStringFunction() {
    super("toString");
  }

  @Override
  public String getDescription() {
    return "Convert to string";
  }

  @Override
  public String getExample() {
    return "toString(id) ~ '123%'";
  }

}
