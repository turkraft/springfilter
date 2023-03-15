package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class SomeFunction extends FilterFunction {

  public SomeFunction() {
    super("some");
  }

}
