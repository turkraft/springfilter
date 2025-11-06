package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ToBigIntegerFunction extends FilterFunction {

  public ToBigIntegerFunction() {
    super("toBigInteger");
  }

  @Override
  public String getDescription() {
    return "Convert to BigInteger";
  }

  @Override
  public String getExample() {
    return "toBigInteger(value) > 1000000";
  }

}
