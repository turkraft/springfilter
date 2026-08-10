package com.turkraft.springfilter.typesafe.builder;

import com.turkraft.springfilter.parser.node.FilterNode;

public class CollectionFieldStep<P extends FilterChain<P>> {

  private final P chain;
  private final String path;

  public CollectionFieldStep(P chain, String path) {
    this.chain = chain;
    this.path = path;
  }

  public P isEmpty() {
    return apply(chain.fb.field(path).isEmpty().get());
  }

  public P isNotEmpty() {
    return apply(chain.fb.field(path).isNotEmpty().get());
  }

  public P isNull() {
    return apply(chain.fb.field(path).isNull().get());
  }

  public P isNotNull() {
    return apply(chain.fb.field(path).isNotNull().get());
  }

  private P apply(FilterNode condition) {
    chain.apply(condition);
    return chain;
  }

}
