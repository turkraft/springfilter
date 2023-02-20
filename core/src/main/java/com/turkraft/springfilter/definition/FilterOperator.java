package com.turkraft.springfilter.definition;

public abstract class FilterOperator implements FilterDefinition {

  private final String token;

  private final int priority;

  public FilterOperator(String token, int priority) {
    this.token = token;
    this.priority = priority;
  }

  public String getToken() {
    return token;
  }

  public int getPriority() {
    return priority;
  }

}
