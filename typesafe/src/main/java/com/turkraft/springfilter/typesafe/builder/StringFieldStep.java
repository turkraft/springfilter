package com.turkraft.springfilter.typesafe.builder;

import com.turkraft.springfilter.parser.node.FilterNode;

public class StringFieldStep<P extends FilterChain<P>> {

  private final P chain;
  private final String path;

  public StringFieldStep(P chain, String path) {
    this.chain = chain;
    this.path = path;
  }

  public P equal(String value) {
    return apply(chain.fb.field(path).equal(chain.fb.input(value)).get());
  }

  public P notEqual(String value) {
    return apply(chain.fb.field(path).notEqual(chain.fb.input(value)).get());
  }

  public P startsWith(String value) {
    return apply(chain.fb.field(path).startsWith(value).get());
  }

  public P endsWith(String value) {
    return apply(chain.fb.field(path).endsWith(value).get());
  }

  public P contains(String value) {
    return apply(chain.fb.field(path).contains(value).get());
  }

  public P like(String pattern) {
    return apply(chain.fb.field(path).like(chain.fb.input(pattern)).get());
  }

  public P isNull() {
    return apply(chain.fb.field(path).isNull().get());
  }

  public P isNotNull() {
    return apply(chain.fb.field(path).isNotNull().get());
  }

  public P isEmpty() {
    return apply(chain.fb.field(path).isEmpty().get());
  }

  public P isNotEmpty() {
    return apply(chain.fb.field(path).isNotEmpty().get());
  }

  private P apply(FilterNode condition) {
    chain.apply(condition);
    return chain;
  }

}
