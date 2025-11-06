package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ProdFunction extends FilterFunction {

  public ProdFunction() {
    super("prod");
  }

  @Override
  public String getDescription() {
    return "Multiply two numbers";
  }

  @Override
  public String getExample() {
    return "prod(price, quantity) > 1000";
  }

}
