package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import org.springframework.stereotype.Component;

@Component
public class InsensitiveLikeOperator extends FilterInfixOperator {

  public InsensitiveLikeOperator() {
    super(new String[]{"~~", "ilike"}, 100);
  }

}
