package com.turkraft.springfilter.parser;

import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.function.UnaryOperator;

public interface ParseContext {

  default UnaryOperator<String> getFieldMapper() {
    return UnaryOperator.identity();
  }

  default UnaryOperator<FilterNode> getNodeMapper() {
    return UnaryOperator.identity();
  }

}
