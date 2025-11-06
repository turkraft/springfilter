package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class TrimFunction extends FilterFunction {

  public TrimFunction() {
    super("trim");
  }

  @Override
  public String getDescription() {
    return "Remove leading and trailing spaces";
  }

  @Override
  public String getExample() {
    return "trim(name) : 'John'";
  }

}
