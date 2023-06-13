package com.turkraft.springfilter.parser;

import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.lang.Nullable;

public interface FilterParser {

  FilterNode parse(String input, @Nullable ParseContext ctx);

  default FilterNode parse(String input) {
    return parse(input, null);
  }

}
