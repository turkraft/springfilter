package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class QuotFunction extends FilterFunction {

  public QuotFunction() {
    super("quot");
  }

  @Override
  public String getDescription() {
    return "Divide two numbers";
  }

  @Override
  public String getExample() {
    return "quot(total, count) > 50";
  }

}
