package com.turkraft.springfilter.typesafe.builder;

import com.turkraft.springfilter.builder.StepWithResult;
import com.turkraft.springfilter.parser.node.FilterNode;

public class EnumFieldStep<P extends FilterChain<P>> {

  private final P chain;
  private final String path;

  public EnumFieldStep(P chain, String path) {
    this.chain = chain;
    this.path = path;
  }

  public <E extends Enum<E>> P equal(E value) {
    return apply(chain.fb.field(path).equal(chain.fb.input(value)).get());
  }

  public <E extends Enum<E>> P notEqual(E value) {
    return apply(chain.fb.field(path).notEqual(chain.fb.input(value)).get());
  }

  public P isNull() {
    return apply(chain.fb.field(path).isNull().get());
  }

  public P isNotNull() {
    return apply(chain.fb.field(path).isNotNull().get());
  }

  @SafeVarargs
  public final <E extends Enum<E>> P in(E... values) {
    StepWithResult[] inputs = new StepWithResult[values.length];
    for (int i = 0; i < values.length; i++) {
      inputs[i] = chain.fb.input(values[i]);
    }
    return apply(chain.fb.field(path).in(chain.fb.collection(inputs)).get());
  }

  @SafeVarargs
  public final <E extends Enum<E>> P notIn(E... values) {
    StepWithResult[] inputs = new StepWithResult[values.length];
    for (int i = 0; i < values.length; i++) {
      inputs[i] = chain.fb.input(values[i]);
    }
    return apply(chain.fb.field(path).notIn(chain.fb.collection(inputs)).get());
  }

  private P apply(FilterNode condition) {
    chain.apply(condition);
    return chain;
  }

}
