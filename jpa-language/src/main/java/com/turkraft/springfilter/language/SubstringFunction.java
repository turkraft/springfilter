package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class SubstringFunction extends FilterFunction {

  public SubstringFunction() {
    super("substring");
  }

  @Override
  public String getDescription() {
    return "Extract substring";
  }

  @Override
  public String getExample() {
    return "substring(name, 1, 4) : 'John'";
  }

}
