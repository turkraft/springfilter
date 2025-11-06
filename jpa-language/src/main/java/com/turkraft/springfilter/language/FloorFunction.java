package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class FloorFunction extends FilterFunction {

  public FloorFunction() {
    super("floor");
  }

  @Override
  public String getDescription() {
    return "Round down to nearest integer";
  }

  @Override
  public String getExample() {
    return "floor(price) : 99";
  }

}
