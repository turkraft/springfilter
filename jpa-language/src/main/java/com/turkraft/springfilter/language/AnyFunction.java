package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class AnyFunction extends FilterFunction {

  public AnyFunction() {
    super("any");
  }

  @Override
  public String getDescription() {
    return "Any value matches (not implemented)";
  }

  @Override
  public String getExample() {
    return "price > any(subquery)";
  }

}
