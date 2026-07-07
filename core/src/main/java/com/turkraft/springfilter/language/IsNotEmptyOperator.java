package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterPostfixOperator;
import org.springframework.stereotype.Component;

@Component
public class IsNotEmptyOperator extends FilterPostfixOperator {

  public IsNotEmptyOperator() {
    super("is not empty", 100);
  }

  @Override
  public String getDescription() {
    return "Collection or string is not empty";
  }

  @Override
  public String getExample() {
    return "orders is not empty";
  }

}
