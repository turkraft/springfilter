package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class ExistsFunction extends FilterFunction {

  protected ExistsFunction() {
    super("exists");
  }

}
