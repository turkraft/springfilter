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
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Service
class FieldTypeResolverImpl implements FieldTypeResolver {

  @Override
  public Class<?> resolve(Class<?> klass, String path) {

    Class<?> currentClass = klass;
    Type currentType = klass;
    boolean mapKeyExpected = false;
    Field previousField = null;

    for (String fieldName : path.split("\\.")) {

      if (fieldName.startsWith("$")) {

        if (previousField != null && isReferenceField(previousField)) {
          return resolveReferenceSegmentType(previousField, fieldName);
        }

        if (mapKeyExpected) {
          Type valueType = getValueTypeOf(currentType);
          currentType = valueType;
          currentClass = normalizeMapValueType(valueType);
          mapKeyExpected = Map.class.isAssignableFrom(currentClass);
          continue;
        }

        return Object.class;
      }

      if (mapKeyExpected) {
        Type valueType = getValueTypeOf(currentType);
        currentType = valueType;
        currentClass = normalizeMapValueType(valueType);
        mapKeyExpected = Map.class.isAssignableFrom(currentClass);
        previousField = null;
        continue;
      }

      Field field = ReflectionUtils.findField(currentClass, fieldName);

      if (field == null) {
        return Object.class;
      }

      currentType = field.getGenericType();
      currentClass = normalize(field);
      mapKeyExpected = Map.class.isAssignableFrom(field.getType());
      previousField = field;

    }

    return currentClass;

  }

  private Field referenceDollarField(Class<?> klass, final String path) {

    if (!hasDollarSegment(path)) {
      return null;
    }

    Class<?> currentClass = klass;
    Field lastField = null;

    for (String segment : path.split("\\.")) {

      if (segment.startsWith("$")) {
        return lastField != null && isReferenceField(lastField) ? lastField : null;
      }

      Field field = ReflectionUtils.findField(currentClass, segment);

      if (field == null) {
        return null;
      }

      lastField = field;
      currentClass = normalize(field);

    }

    return null;

  }

  @Override
  public boolean isCollectionDbRefField(Class<?> klass, final String path) {
    Field field = referenceDollarField(klass, path);
    return field != null && Collection.class.isAssignableFrom(field.getType());
  }

  @Override
  public boolean isReferenceDollarField(Class<?> klass, final String path) {
    return referenceDollarField(klass, path) != null;
  }

  @Override
  public String storedFieldPath(Class<?> klass, final String path) {

    if (!hasDollarSegment(path)) {
      return path;
    }

    String[] segments = path.split("\\.", -1);
    StringBuilder result = new StringBuilder();
    Class<?> currentClass = klass;
    Field previousField = null;

    for (int i = 0; i < segments.length; i++) {

      String segment = segments[i];

      if (segment.startsWith("$")) {

        boolean dropped = previousField != null
            && previousField.isAnnotationPresent(DocumentReference.class)
            && "$id".equals(segment);

        if (!dropped) {
          appendSegment(result, segment);
        }

        previousField = null;
        continue;

      }

      appendSegment(result, segment);

      Field field = ReflectionUtils.findField(currentClass, segment);

      if (field == null) {
        for (int j = i + 1; j < segments.length; j++) {
          appendSegment(result, segments[j]);
        }
        return result.toString();
      }

      previousField = field;
      currentClass = normalize(field);

    }

    return result.toString();

  }

  private static void appendSegment(StringBuilder builder, String segment) {
    if (builder.length() > 0) {
      builder.append('.');
    }
    builder.append(segment);
  }

  private static boolean isReferenceField(Field field) {
    return field.isAnnotationPresent(DBRef.class)
        || field.isAnnotationPresent(DocumentReference.class);
  }

  private Class<?> resolveReferenceSegmentType(Field referenceField, String segment) {

    if ("$id".equals(segment)) {
      return resolveReferencedIdType(normalize(referenceField));
    }

    if (referenceField.isAnnotationPresent(DBRef.class)
        && ("$ref".equals(segment) || "$db".equals(segment))) {
      return String.class;
    }

    return Object.class;

  }

  private Class<?> resolveReferencedIdType(Class<?> referencedEntityClass) {

    if (referencedEntityClass == null || Object.class.equals(referencedEntityClass)) {
      return CustomObjectId.class;
    }

    Field idField = null;

    for (Class<?> current = referencedEntityClass; current != null && idField == null;
        current = current.getSuperclass()) {
      for (Field field : current.getDeclaredFields()) {
        if (field.isAnnotationPresent(Id.class)) {
          idField = field;
          break;
        }
      }
    }

    if (idField == null) {
      idField = ReflectionUtils.findField(referencedEntityClass, "id");
    }

    if (idField == null) {
      return CustomObjectId.class;
    }

    Class<?> idType = idField.getType();

    if (String.class.equals(idType) || ObjectId.class.equals(idType)) {
      return CustomObjectId.class;
    }

    if (UUID.class.equals(idType)) {
      return CustomUUID.class;
    }

    return idType;

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

    if (field
        .getType()
        .equals(ObjectId.class)) {
      return CustomObjectId.class;
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
    if (ObjectId.class.equals(raw)) {
      return CustomObjectId.class;
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
