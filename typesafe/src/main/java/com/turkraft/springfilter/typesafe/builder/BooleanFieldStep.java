package com.turkraft.springfilter.typesafe.builder;

import com.turkraft.springfilter.parser.node.FilterNode;

public class BooleanFieldStep<P extends FilterChain<P>> {

  private final P chain;
  private final String path;

  public BooleanFieldStep(P chain, String path) {
    this.chain = chain;
    this.path = path;
  }

  public P isTrue() {
    return apply(chain.fb.field(path).equal(chain.fb.input(true)).get());
  }

  public P isFalse() {
    return apply(chain.fb.field(path).equal(chain.fb.input(false)).get());
  }

  private P apply(FilterNode condition) {
    chain.apply(condition);
    return chain;
  }

}
