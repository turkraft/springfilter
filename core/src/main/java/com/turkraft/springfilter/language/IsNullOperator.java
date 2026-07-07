package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterPostfixOperator;
import org.springframework.stereotype.Component;

@Component
public class IsNullOperator extends FilterPostfixOperator {

  public IsNullOperator() {
    super("is null", 100);
  }

  @Override
  public String getDescription() {
    return "Value is null";
  }

  @Override
  public String getExample() {
    return "deletedAt is null";
  }

}
