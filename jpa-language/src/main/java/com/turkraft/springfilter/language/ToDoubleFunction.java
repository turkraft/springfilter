package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ToDoubleFunction extends FilterFunction {

  public ToDoubleFunction() {
    super("toDouble");
  }

  @Override
  public String getDescription() {
    return "Convert to double";
  }

  @Override
  public String getExample() {
    return "toDouble(value) > 99.99";
  }

}
