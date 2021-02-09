package com.torshid.compiler.node;

import java.util.function.Function;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
public abstract class Node {

  public abstract Node transform(Node parent);

  public abstract String generate();

  public final <T> T generate(Function<Node, T> func) {
    return func.apply(this);
  }

}
