package com.turkraft.springfilter.compiler.node;

import com.turkraft.springfilter.generator.Generator;
import com.turkraft.springfilter.generator.QueryGenerator;

public interface IExpression {

  IExpression transform(IExpression parent);

  default <T> T generate(Generator<T> generator) {
    return generator.generate(this);
  }

  default String generate() {
    return QueryGenerator.run(this);
  }

}
