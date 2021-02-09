package com.torshid.compiler.node;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
public abstract class Root<N extends Root<N>> extends Node {

  @Override
  public abstract N transform(Node parent);

}
