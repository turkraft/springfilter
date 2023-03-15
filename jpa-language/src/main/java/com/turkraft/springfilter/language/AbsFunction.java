package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class AbsFunction extends FilterFunction {

  public AbsFunction() {
    super("abs");
  }

}
