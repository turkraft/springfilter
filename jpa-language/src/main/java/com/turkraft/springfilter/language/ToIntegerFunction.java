package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ToIntegerFunction extends FilterFunction {

  public ToIntegerFunction() {
    super("toInteger");
  }

  @Override
  public String getDescription() {
    return "Convert to integer";
  }

  @Override
  public String getExample() {
    return "toInteger(code) > 100";
  }

}
