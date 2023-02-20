package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterPlaceholder;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldPlaceholder extends FilterPlaceholder {

  protected HelloWorldPlaceholder() {
    super("hello");
  }

}
