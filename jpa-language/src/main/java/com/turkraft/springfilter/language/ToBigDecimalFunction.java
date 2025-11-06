package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ToBigDecimalFunction extends FilterFunction {

  public ToBigDecimalFunction() {
    super("toBigDecimal");
  }

  @Override
  public String getDescription() {
    return "Convert to BigDecimal";
  }

  @Override
  public String getExample() {
    return "toBigDecimal(amount) > 1000.50";
  }

}
