package com.turkraft.springfilter.parser;

import java.util.function.UnaryOperator;

public interface ParseContext {

  default UnaryOperator<String> getFieldMapper() {
    return UnaryOperator.identity();
  }

}
