package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class XorOperator extends FilterInfixOperator {

  public XorOperator() {
    super(new String[]{"xor"}, 25);
  }

  @Override
  public String getDescription() {
    return "Exactly one condition is true";
  }

  @Override
  public String getExample() {
    return "a : 1 xor b : 2";
  }

}
