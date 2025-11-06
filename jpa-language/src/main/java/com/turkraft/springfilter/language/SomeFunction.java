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
    return "Some value matches (not implemented)";
  }

  @Override
  public String getExample() {
    return "price > some(subquery)";
  }

}
