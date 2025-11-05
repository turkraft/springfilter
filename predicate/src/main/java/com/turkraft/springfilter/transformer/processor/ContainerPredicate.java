package com.turkraft.springfilter.transformer.processor;

import java.util.function.Predicate;

public class ContainerPredicate<T> implements Predicate<T> {

  private final Object value;

  public ContainerPredicate(Object value) {
    this.value = value;
  }

  @Override
  public boolean test(T t) {
    throw new IllegalStateException();
  }

  public Object getValue() {
    return value;
  }

}
