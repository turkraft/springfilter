package com.turkraft.springfilter.compiler.node;

import java.util.function.Function;

public interface INode {

  INode transform(INode parent);

  String generate();

  default <T> T generate(Function<INode, T> func) {
    return func.apply(this);
  }

}
