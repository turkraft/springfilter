package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class NotInOperator extends FilterInfixOperator {

  public NotInOperator() {
    super("not in", 100);
  }

  @Override
  public String getDescription() {
    return "Value not in list";
  }

  @Override
  public String getExample() {
    return "status not in ('deleted', 'archived')";
  }

}
