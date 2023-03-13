package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class CountDistinctFunction extends FilterFunction {

  protected CountDistinctFunction() {
    super("countDistinct");
  }

}
