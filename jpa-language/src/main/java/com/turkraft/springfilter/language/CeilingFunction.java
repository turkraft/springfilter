package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class CeilingFunction extends FilterFunction {

  public CeilingFunction() {
    super("ceiling");
  }

  @Override
  public String getDescription() {
    return "Round up to nearest integer";
  }

  @Override
  public String getExample() {
    return "ceiling(price) : 100";
  }

}
