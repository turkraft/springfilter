package com.turkraft.springfilter.parser;

import com.turkraft.springfilter.parser.node.FilterNode;

public interface FilterParser {

  FilterNode parse(String input);

}
