package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class LikeOperator extends FilterInfixOperator {

  public LikeOperator() {
    super(new String[]{"~", "like"}, 100);
  }

}
