package com.turkraft.springfilter.transformer.processor;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public class FieldAccessPredicate implements Predicate<Object> {

  private final String fieldPath;

  public FieldAccessPredicate(String fieldPath) {
    this.fieldPath = fieldPath;
  }

  @Override
  public boolean test(Object entity) {
    throw new UnsupportedOperationException(
        "FieldAccessPredicate cannot be directly tested. It must be composed with an operation.");
  }

  public Object getValue(Object entity) {
    if (entity == null) {
      return null;
    }

    String[] parts = fieldPath.split("\\.");
    Object current = entity;

    for (String part : parts) {
      if (current == null) {
        return null;
      }

      current = getFieldValue(current, part);
    }

    return current;
  }

  private Object getFieldValue(Object object, String fieldName) {
    try {
      Field field = findField(object.getClass(), fieldName);
      if (field == null) {
        throw new IllegalArgumentException(
            "Field '" + fieldName + "' not found in " + object.getClass());
      }

      field.setAccessible(true);
      return field.get(object);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Cannot access field '" + fieldName + "'", e);
    }
  }

  private Field findField(Class<?> clazz, String fieldName) {
    Class<?> current = clazz;

    while (current != null && current != Object.class) {
      try {
        return current.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        current = current.getSuperclass();
      }
    }

    return null;
  }

  public String getFieldPath() {
    return fieldPath;
  }

}
