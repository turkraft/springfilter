package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class LocalDateFunction extends FilterFunction {

  public LocalDateFunction() {
    super("localDate");
  }

  @Override
  public String getDescription() {
    return "Current local date";
  }

  @Override
  public String getExample() {
    return "dueDate : localDate()";
  }

}
