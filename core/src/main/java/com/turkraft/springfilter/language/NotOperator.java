package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterPrefixOperator;
import org.springframework.stereotype.Component;

@Component
public class NotOperator extends FilterPrefixOperator {

  public NotOperator() {
    super("not", 150);
  }

  @Override
  public String getDescription() {
    return "Negate condition";
  }

  @Override
  public String getExample() {
    return "not status : 'deleted'";
  }

}
