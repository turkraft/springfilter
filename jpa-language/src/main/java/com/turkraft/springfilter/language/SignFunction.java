package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class SignFunction extends FilterFunction {

  public SignFunction() {
    super("sign");
  }

  @Override
  public String getDescription() {
    return "Sign of number (-1, 0, or 1)";
  }

  @Override
  public String getExample() {
    return "sign(balance) : 1";
  }

}
