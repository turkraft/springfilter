package com.turkraft.springfilter.typesafe.builder;

import com.turkraft.springfilter.language.SizeFunction;
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

  public P hasSizeGreaterThan(int value) {
    return apply(chain.fb.function(new SizeFunction(), chain.fb.field(path))
        .greaterThan(chain.fb.input(value)).get());
  }

  public P hasSizeGreaterThanOrEqual(int value) {
    return apply(chain.fb.function(new SizeFunction(), chain.fb.field(path))
        .greaterThanOrEqual(chain.fb.input(value)).get());
  }

  public P hasSizeLessThan(int value) {
    return apply(chain.fb.function(new SizeFunction(), chain.fb.field(path))
        .lessThan(chain.fb.input(value)).get());
  }

  public P hasSizeLessThanOrEqual(int value) {
    return apply(chain.fb.function(new SizeFunction(), chain.fb.field(path))
        .lessThanOrEqual(chain.fb.input(value)).get());
  }

  public P hasSizeEqualTo(int value) {
    return apply(chain.fb.function(new SizeFunction(), chain.fb.field(path))
        .equal(chain.fb.input(value)).get());
  }

  private P apply(FilterNode condition) {
    chain.apply(condition);
    return chain;
  }

}
