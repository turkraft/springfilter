package com.turkraft.springfilter.generator;

import static org.apache.commons.lang3.reflect.FieldUtils.getField;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

// source: https://github.com/RutledgePaulV/rest-query-engine

public class EntityFieldTypeResolver {

  public static Class<?> resolve(String path, Class<?> root) {
    String[] splitField = path.split("\\.", 2);
    if (splitField.length == 1) {
      return normalize(getField(root, splitField[0], true));
    } else {
      return resolve(splitField[1], normalize(getField(root, splitField[0], true)));
    }
  }

  private static Class<?> normalize(Field field) {
    if (Collection.class.isAssignableFrom(field.getType())) {
      return getFirstTypeParameterOf(field);
    } else if (field.getType().isArray()) {
      return field.getType().getComponentType();
    } else {
      return field.getType();
    }
  }

  private static Class<?> getFirstTypeParameterOf(Field field) {
    return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
  }

}
