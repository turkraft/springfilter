package com.turkraft.springfilter.helper;

import java.lang.reflect.Field;

public interface FieldTypeResolver {

  Class<?> resolve(Class<?> klass, String path);

  Field getField(Class<?> klass, String path);

  default boolean isCollectionDbRefField(Class<?> klass, String path) {
    return false;
  }

  default boolean isReferenceDollarField(Class<?> klass, String path) {
    return false;
  }

  default String storedFieldPath(Class<?> klass, String path) {
    return path;
  }

  default boolean hasDollarSegment(String path) {
    if (path.indexOf('$') < 0) {
      return false;
    }
    for (String segment : path.split("\\.")) {
      if (!segment.isEmpty() && segment.charAt(0) == '$') {
        return true;
      }
    }
    return false;
  }

}
