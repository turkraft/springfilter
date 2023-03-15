package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class LocalTimeFunction extends FilterFunction {

  public LocalTimeFunction() {
    super("localTime");
  }

}
