package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.converter.StringCustomObjectIdConverter.CustomObjectId;
import com.turkraft.springfilter.converter.StringCustomUUIDConverter.CustomUUID;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;

// source: https://github.com/RutledgePaulV/rest-query-engine

// this class is only used by the BsonGenerator, and adds the lang3 dependency unfortunately
// it should be possible to get rid of it and use reflection/LambdaMetafactory

@Service
class FieldTypeResolverImpl implements FieldTypeResolver {

  @Override

  public Class<?> resolve(Class<?> root, String path) {
    String[] splitField = path.split("\\.", 2);
    if (splitField.length == 1) {
      return normalize(getField(root, splitField[0]));
    } else {
      return resolve(normalize(getField(root, splitField[0])), splitField[1]);
    }
  }

  @Override
  public Field getField(final Class<?> klass, final String fieldName) {
    Field field = org.apache.commons.lang3.reflect.FieldUtils.getField(klass, fieldName, true);
    if (field == null) {
      throw new IllegalArgumentException("Could not find field '" + fieldName + "' in " + klass);
    }
    return field;
  }

  private Class<?> normalize(Field field) {

    if (field.isAnnotationPresent(Id.class) && field.getType().equals(String.class)) {
      return CustomObjectId.class;
    }
    
    if (field.getType().equals(UUID.class)) {
      return CustomUUID.class;
    }

    if (Collection.class.isAssignableFrom(field.getType())) {
      return getFirstTypeParameterOf(field);
    } else if (field.getType().isArray()) {
      return field.getType().getComponentType();
    } else {
      return field.getType();
    }

  }

  private Class<?> getFirstTypeParameterOf(Field field) {
    return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
  }

}
