package com.turkraft.springfilter.parser;

import java.util.function.UnaryOperator;

public class ParseContextImpl implements ParseContext {

  private final UnaryOperator<String> fieldMapper;

  public ParseContextImpl(UnaryOperator<String> fieldMapper) {
    this.fieldMapper = fieldMapper;
  }

  @Override
  public UnaryOperator<String> getFieldMapper() {
    return fieldMapper;
  }

}
