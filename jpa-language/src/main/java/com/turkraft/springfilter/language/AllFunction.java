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
    return "All values match (not implemented)";
  }

  @Override
  public String getExample() {
    return "price > all(subquery)";
  }

}
