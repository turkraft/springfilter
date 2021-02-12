package com.springfilter.compiler.node;

public interface IRoot<N extends IRoot<N>> extends INode {

  @Override
  N transform(INode parent);

}
