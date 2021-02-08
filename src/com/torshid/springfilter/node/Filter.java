package com.torshid.springfilter.node;

import com.torshid.compiler.node.Node;
import com.torshid.compiler.node.Root;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Filter extends Root<Filter> {

  private Expression body;

  @Override
  public Filter transform(Node parent) {
    body = (Expression) body.transform(this);
    return this;
  }

  @Override
  public String generate() {
    return body.generate();
  }

}
