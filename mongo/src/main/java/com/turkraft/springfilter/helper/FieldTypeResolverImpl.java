package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.converter.StringCustomObjectIdConverter.CustomObjectId;
import com.turkraft.springfilter.converter.StringCustomUUIDConverter.CustomUUID;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Service
class FieldTypeResolverImpl implements FieldTypeResolver {

  @Override
  public Class<?> resolve(Class<?> klass, String path) {

    Class<?> currentClass = klass;
    Type currentType = klass;
    boolean mapKeyExpected = false;

    for (String fieldName : path.split("\\.")) {

      if (mapKeyExpected) {
        Type valueType = getValueTypeOf(currentType);
        currentType = valueType;
        currentClass = normalizeMapValueType(valueType);
        mapKeyExpected = Map.class.isAssignableFrom(currentClass);
        continue;
      }

      Field field = ReflectionUtils.findField(currentClass, fieldName);

      if (field == null) {
        return Object.class;
      }

      currentType = field.getGenericType();
      currentClass = normalize(field);
      mapKeyExpected = Map.class.isAssignableFrom(field.getType());

    }

    return currentClass;

  }

  @Override
  public Field getField(Class<?> klass, final String path) {

    String[] fieldNames = path.split("\\.");

    Field lastField = null;

    for (int i = 0; i < fieldNames.length; i++) {

      lastField = ReflectionUtils.findField(klass, fieldNames[i]);

      if (lastField != null) {

        if (Map.class.isAssignableFrom(lastField.getType()) && i < fieldNames.length - 1) {
          return null;
        }

        klass = normalize(lastField);
      } else {
        return null;
      }

    }

    return lastField;

  }

  private Class<?> normalize(Field field) {

    if (field == null) {
      return Object.class;
    }

    if (field.isAnnotationPresent(Id.class) && field
        .getType()
        .equals(String.class)) {
      return CustomObjectId.class;
    }

    if (field
        .getType()
        .equals(UUID.class)) {
      return CustomUUID.class;
    }

    if (Collection.class.isAssignableFrom(field.getType())) {
      return getRawClass(getTypeArgumentOf(field.getGenericType(), 0));
    } else if (field
        .getType()
        .isArray()) {
      return field
          .getType()
          .getComponentType();
    } else {
      return field.getType();
    }

  }

  private static Class<?> normalizeMapValueType(Type valueType) {
    Class<?> raw = getRawClass(valueType);
    if (UUID.class.equals(raw)) {
      return CustomUUID.class;
    }
    if (Collection.class.isAssignableFrom(raw)) {
      return getRawClass(getTypeArgumentOf(valueType, 0));
    }
    if (raw.isArray()) {
      return raw.getComponentType();
    }
    return raw;
  }

  private static Type getValueTypeOf(Type mapType) {
    return getTypeArgumentOf(mapType, 1);
  }

  private static Type getTypeArgumentOf(Type type, int index) {
    if (type instanceof ParameterizedType parameterizedType
        && parameterizedType
        .getActualTypeArguments().length > index) {
      return parameterizedType
          .getActualTypeArguments()[index];
    }
    return Object.class;
  }

  private static Class<?> getRawClass(Type type) {
    if (type instanceof Class<?> klass) {
      return klass;
    }
    if (type instanceof ParameterizedType parameterizedType) {
      return getRawClass(parameterizedType.getRawType());
    }
    if (type instanceof WildcardType wildcardType
        && wildcardType
        .getUpperBounds().length > 0) {
      return getRawClass(wildcardType.getUpperBounds()[0]);
    }
    return Object.class;
  }

}
