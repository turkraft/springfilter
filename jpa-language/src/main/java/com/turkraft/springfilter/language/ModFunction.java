package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ModFunction extends FilterFunction {

  public ModFunction() {
    super("mod");
  }

  @Override
  public String getDescription() {
    return "Modulus (remainder after division)";
  }

  @Override
  public String getExample() {
    return "mod(id, 10) : 0";
  }

}
