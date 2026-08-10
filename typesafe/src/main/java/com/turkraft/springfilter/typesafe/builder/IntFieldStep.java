package com.turkraft.springfilter.typesafe.builder;

import com.turkraft.springfilter.parser.node.FilterNode;

public class IntFieldStep<P extends FilterChain<P>> {

  private final P chain;
  private final String path;

  public IntFieldStep(P chain, String path) {
    this.chain = chain;
    this.path = path;
  }

  public P equal(int value) {
    return apply(chain.fb.field(path).equal(chain.fb.input(value)).get());
  }

  public P notEqual(int value) {
    return apply(chain.fb.field(path).notEqual(chain.fb.input(value)).get());
  }

  public P greaterThan(int value) {
    return apply(chain.fb.field(path).greaterThan(chain.fb.input(value)).get());
  }

  public P greaterThanOrEqual(int value) {
    return apply(chain.fb.field(path).greaterThanOrEqual(chain.fb.input(value)).get());
  }

  public P lessThan(int value) {
    return apply(chain.fb.field(path).lessThan(chain.fb.input(value)).get());
  }

  public P lessThanOrEqual(int value) {
    return apply(chain.fb.field(path).lessThanOrEqual(chain.fb.input(value)).get());
  }

  public P between(int lower, int upper) {
    return apply(chain.fb.field(path).between(chain.fb.input(lower), chain.fb.input(upper)).get());
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
