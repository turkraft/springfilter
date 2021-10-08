package com.turkraft.springfilter.parser;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

// source: https://github.com/RutledgePaulV/rest-query-engine

// this class is only used by the BsonGenerator, and adds the lang3 dependency unfortunately
// it should be possible to get rid of it and use reflection/LambdaMetafactory

public class EntityFieldTypeResolver {

  public static Class<?> resolve(String path, Class<?> root) {
    String[] splitField = path.split("\\.", 2);
    if (splitField.length == 1) {
      return normalize(getField(root, splitField[0]));
    } else {
      return resolve(splitField[1], normalize(getField(root, splitField[0])));
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

  public static Field getField(final Class<?> klass, final String fieldName) {
    Field field = org.apache.commons.lang3.reflect.FieldUtils.getField(klass, fieldName, true);
    if (field == null) {
      throw new IllegalArgumentException("Could not find field '" + fieldName + "' in " + klass);
    }
    return field;
  }

}
