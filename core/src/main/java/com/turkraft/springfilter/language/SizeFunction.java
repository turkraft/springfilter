package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class SizeFunction extends FilterFunction {

  protected SizeFunction() {
    super("size");
  }

  @Override
  public String getDescription() {
    return "Collection element count";
  }

  @Override
  public String getExample() {
    return "size(orders) > 5";
  }

}
