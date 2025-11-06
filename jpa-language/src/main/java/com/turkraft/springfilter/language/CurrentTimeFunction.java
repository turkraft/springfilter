package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class CurrentTimeFunction extends FilterFunction {

  public CurrentTimeFunction() {
    super("currentTime");
  }

  @Override
  public String getDescription() {
    return "Current database time";
  }

  @Override
  public String getExample() {
    return "eventTime > currentTime()";
  }

}
