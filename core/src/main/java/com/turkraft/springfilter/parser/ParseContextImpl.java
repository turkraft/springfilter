package com.turkraft.springfilter.parser;

import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.function.UnaryOperator;
import org.springframework.lang.Nullable;

public class ParseContextImpl implements ParseContext {

  private UnaryOperator<String> fieldMapper;

  private UnaryOperator<FilterNode> nodeMapper;

  public ParseContextImpl() {
  }

  public ParseContextImpl(@Nullable UnaryOperator<String> fieldMapper,
      @Nullable UnaryOperator<FilterNode> nodeMapper) {
    this.fieldMapper = fieldMapper;
    this.nodeMapper = nodeMapper;
  }

  @Deprecated(since = "3.1.3")
  public ParseContextImpl(@Nullable UnaryOperator<String> fieldMapper) {
    this.fieldMapper = fieldMapper;
  }

  @Override
  public UnaryOperator<String> getFieldMapper() {
    if (fieldMapper == null) {
      return UnaryOperator.identity();
    }
    return fieldMapper;
  }

  public void setFieldMapper(UnaryOperator<String> fieldMapper) {
    this.fieldMapper = fieldMapper;
  }

  @Override
  public UnaryOperator<FilterNode> getNodeMapper() {
    if (nodeMapper == null) {
      return UnaryOperator.identity();
    }
    return nodeMapper;
  }

  public void setNodeMapper(
      UnaryOperator<FilterNode> nodeMapper) {
    this.nodeMapper = nodeMapper;
  }

}
