package com.turkraft.springfilter.generator;

import javax.persistence.criteria.Root;

public class ExpressionDatabasePath {

  private final ExpressionDatabasePath parent;

  private final Root<?> value;

  public ExpressionDatabasePath(ExpressionDatabasePath parent, Root<?> value) {
    this.parent = parent;
    this.value = value;
  }

  public ExpressionDatabasePath getParent() {
    return parent;
  }

  public Root<?> getValue() {
    return value;
  }

}
