package com.turkraft.springfilter.typesafe.builder;

import com.turkraft.springfilter.parser.node.FilterNode;

public class DoubleFieldStep<P extends FilterChain<P>> {

  private final P chain;
  private final String path;

  public DoubleFieldStep(P chain, String path) {
    this.chain = chain;
    this.path = path;
  }

  public P equal(double value) {
    return apply(chain.fb.field(path).equal(chain.fb.input(value)).get());
  }

  public P notEqual(double value) {
    return apply(chain.fb.field(path).notEqual(chain.fb.input(value)).get());
  }

  public P greaterThan(double value) {
    return apply(chain.fb.field(path).greaterThan(chain.fb.input(value)).get());
  }

  public P greaterThanOrEqual(double value) {
    return apply(chain.fb.field(path).greaterThanOrEqual(chain.fb.input(value)).get());
  }

  public P lessThan(double value) {
    return apply(chain.fb.field(path).lessThan(chain.fb.input(value)).get());
  }

  public P lessThanOrEqual(double value) {
    return apply(chain.fb.field(path).lessThanOrEqual(chain.fb.input(value)).get());
  }

  public P between(double lower, double upper) {
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
