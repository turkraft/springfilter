package com.torshid.compiler.node;

public interface Root<N extends Root<N>> extends INode {

  @Override
  N transform(INode parent);

}
