package com.turkraft.springfilter.pagesort;

public final class FieldsFilterContext {

  private static final ThreadLocal<FieldsExpression> CURRENT = new ThreadLocal<>();

  private FieldsFilterContext() {}

  public static void set(FieldsExpression fields) {
    CURRENT.set(fields);
  }

  public static FieldsExpression get() {
    return CURRENT.get();
  }

  public static void clear() {
    CURRENT.remove();
  }

}
