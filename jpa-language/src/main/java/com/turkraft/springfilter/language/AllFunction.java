package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class AllFunction extends FilterFunction {

  public AllFunction() {
    super("all");
  }

  @Override
  public String getDescription() {
    return "True when the comparison holds for every element of the collection";
  }

  @Override
  public String getExample() {
    return "integer > all(integers)";
  }

}
