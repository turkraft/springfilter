package com.turkraft.springfilter.definition;

public abstract class FilterFunction implements FilterDefinition {

  private final String name;

  protected FilterFunction(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
