package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class SomeFunction extends FilterFunction {

  public SomeFunction() {
    super("some");
  }

  @Override
  public String getDescription() {
    return "True when the comparison holds for at least one element of the collection";
  }

  @Override
  public String getExample() {
    return "integer > some(integers)";
  }

}
