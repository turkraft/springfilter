package com.turkraft.springfilter.definition;

public abstract class FilterOperator implements FilterDefinition {

  private final String[] tokens;

  private final int priority;

  public FilterOperator(String[] tokens, int priority) {
    this.tokens = tokens;
    this.priority = priority;
  }

  public FilterOperator(String token, int priority) {
    this(new String[]{token}, priority);
  }

  public String[] getTokens() {
    return tokens;
  }

  public String getToken() {
    return tokens[0];
  }

  public int getPriority() {
    return priority;
  }

}
