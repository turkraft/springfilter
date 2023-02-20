package com.turkraft.springfilter.language;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.definition.FilterInfixOperator;

@Component
public class LikeOperator extends FilterInfixOperator {

  public LikeOperator() {
    super("~", 100);
  }

}
