package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterPostfixOperator;
import org.springframework.stereotype.Component;

@Component
public class IsNotNullOperator extends FilterPostfixOperator {

  public IsNotNullOperator() {
    super("is not null", 100);
  }

  @Override
  public String getDescription() {
    return "Value is not null";
  }

  @Override
  public String getExample() {
    return "email is not null";
  }

}
