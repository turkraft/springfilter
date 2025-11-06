package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ConcatFunction extends FilterFunction {

  public ConcatFunction() {
    super("concat");
  }

  @Override
  public String getDescription() {
    return "Concatenate strings";
  }

  @Override
  public String getExample() {
    return "concat(firstName, ' ', lastName) ~ 'John%'";
  }

}
