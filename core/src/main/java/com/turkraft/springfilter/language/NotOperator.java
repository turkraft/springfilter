package com.turkraft.springfilter.language;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.definition.FilterPrefixOperator;

@Component
public class NotOperator extends FilterPrefixOperator {

  public NotOperator() {
    super("not", 150);
  }

}
