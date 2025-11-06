package com.turkraft.springfilter.openapi.introspection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class EntityIntrospector {

  private final Map<Class<?>, EntitySchema> cache = new ConcurrentHashMap<>();

  private static final int MAX_DEPTH = 3;

  public EntitySchema introspect(Class<?> entityClass) {
    return cache.computeIfAbsent(entityClass, this::doIntrospect);
  }

  private EntitySchema doIntrospect(Class<?> entityClass) {
    EntitySchema schema = new EntitySchema(entityClass);
    introspectFields(entityClass, schema, "", 0, new HashSet<>());
    return schema;
  }

  private void introspectFields(Class<?> clazz, EntitySchema schema, String prefix, int depth,
      Set<Class<?>> visited) {

    if (depth >= MAX_DEPTH || visited.contains(clazz)) {
      return;
    }

    visited.add(clazz);

    Class<?> currentClass = clazz;
    while (currentClass != null && currentClass != Object.class) {
      for (Field field : currentClass.getDeclaredFields()) {
        if (shouldSkipField(field)) {
          continue;
        }

        String fieldPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();
        FieldInfo fieldInfo = analyzeField(field, fieldPath);

        schema.addField(fieldInfo);

        if (fieldInfo.isRelation() || fieldInfo.isEmbedded()) {
          introspectFields(fieldInfo.getTargetType(), schema, fieldPath, depth + 1,
              new HashSet<>(visited));
        }
      }
      currentClass = currentClass.getSuperclass();
    }

  }

  private boolean shouldSkipField(Field field) {
    int modifiers = field.getModifiers();
    return Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers);
  }

  private FieldInfo analyzeField(Field field, String path) {

    Class<?> fieldType = field.getType();
    FieldInfo info = new FieldInfo(path, fieldType);

    try {
      if (field.isAnnotationPresent(
          Class
              .forName("jakarta.persistence.ManyToOne")
              .asSubclass(java.lang.annotation.Annotation.class))
          || field.isAnnotationPresent(
          Class
              .forName("jakarta.persistence.OneToOne")
              .asSubclass(java.lang.annotation.Annotation.class))) {
        info.setRelation(true);
        info.setRelationType(RelationType.TO_ONE);
      } else if (field.isAnnotationPresent(
          Class
              .forName("jakarta.persistence.OneToMany")
              .asSubclass(java.lang.annotation.Annotation.class))
          || field.isAnnotationPresent(
          Class
              .forName("jakarta.persistence.ManyToMany")
              .asSubclass(java.lang.annotation.Annotation.class))) {
        info.setRelation(true);
        info.setRelationType(RelationType.TO_MANY);
        info.setCollection(true);
        info.setTargetType(extractCollectionType(field));
      } else if (field.isAnnotationPresent(
          Class
              .forName("jakarta.persistence.ElementCollection")
              .asSubclass(
                  java.lang.annotation.Annotation.class))) {
        info.setCollection(true);
        info.setTargetType(extractCollectionType(field));
      } else if (field.isAnnotationPresent(
          Class
              .forName("jakarta.persistence.Embedded")
              .asSubclass(java.lang.annotation.Annotation.class))) {
        info.setEmbedded(true);
      }
    } catch (ClassNotFoundException ignored) {
    }

    if (fieldType.isPrimitive() || isWrapperType(fieldType)) {
      info.setFieldCategory(FieldCategory.PRIMITIVE);
    } else if (fieldType.isEnum()) {
      info.setFieldCategory(FieldCategory.ENUM);
      info.setEnumValues(getEnumValues(fieldType));
    } else if (isDateType(fieldType)) {
      info.setFieldCategory(FieldCategory.DATE);
    } else if (CharSequence.class.isAssignableFrom(fieldType)) {
      info.setFieldCategory(FieldCategory.STRING);
    } else if (Collection.class.isAssignableFrom(fieldType)) {
      info.setCollection(true);
      info.setTargetType(extractCollectionType(field));
      info.setFieldCategory(FieldCategory.COLLECTION);
    } else if (Map.class.isAssignableFrom(fieldType)) {
      info.setCollection(true);
      info.setFieldCategory(FieldCategory.MAP);
    } else {
      if (!info.isRelation()) {
        info.setEmbedded(true);
      }
      info.setFieldCategory(FieldCategory.OBJECT);
    }

    return info;

  }

  private boolean isWrapperType(Class<?> type) {
    return type == Integer.class || type == Long.class || type == Double.class
        || type == Float.class || type == Boolean.class || type == Byte.class
        || type == Short.class || type == Character.class;
  }

  private boolean isDateType(Class<?> type) {
    return Date.class.isAssignableFrom(type) || Temporal.class.isAssignableFrom(type)
        || type == LocalDate.class || type == LocalDateTime.class;
  }

  private Class<?> extractCollectionType(Field field) {
    try {
      Type genericType = field.getGenericType();
      if (genericType instanceof ParameterizedType parameterizedType) {
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
          return (Class<?>) typeArguments[0];
        }
      }
    } catch (Exception ignored) {
    }
    return Object.class;
  }

  private List<String> getEnumValues(Class<?> enumClass) {
    List<String> values = new ArrayList<>();
    if (enumClass.isEnum()) {
      Object[] constants = enumClass.getEnumConstants();
      if (constants != null) {
        for (Object constant : constants) {
          if (constant != null) {
            String value = constant.toString();
            if (value != null) {
              values.add(value);
            }
          }
        }
      }
    }
    return values;
  }

  public void clearCache() {
    cache.clear();
  }

  public static class EntitySchema {

    private final Class<?> entityClass;
    private final Map<String, FieldInfo> fields = new HashMap<>();

    public EntitySchema(Class<?> entityClass) {
      this.entityClass = entityClass;
    }

    public void addField(FieldInfo fieldInfo) {
      fields.put(fieldInfo.getPath(), fieldInfo);
    }

    public Map<String, FieldInfo> getFields() {
      return fields;
    }

    public Class<?> getEntityClass() {
      return entityClass;
    }

    public List<FieldInfo> getRootFields() {
      List<FieldInfo> rootFields = new ArrayList<>();
      for (FieldInfo field : fields.values()) {
        if (!field
            .getPath()
            .contains(".")) {
          rootFields.add(field);
        }
      }
      return rootFields;
    }

    public List<FieldInfo> getNestedFields(String prefix) {
      List<FieldInfo> nestedFields = new ArrayList<>();
      String searchPrefix = prefix + ".";
      for (FieldInfo field : fields.values()) {
        if (field
            .getPath()
            .startsWith(searchPrefix)) {
          nestedFields.add(field);
        }
      }
      return nestedFields;
    }
  }

  public static class FieldInfo {

    private final String path;
    private final Class<?> type;
    private Class<?> targetType;
    private boolean isCollection;
    private boolean isRelation;
    private RelationType relationType;
    private boolean isEmbedded;
    private FieldCategory fieldCategory;
    private List<String> enumValues;

    public FieldInfo(String path, Class<?> type) {
      this.path = path;
      this.type = type;
    }

    public String getPath() {
      return path;
    }

    public Class<?> getType() {
      return type;
    }

    public Class<?> getTargetType() {
      return targetType != null ? targetType : type;
    }

    public void setTargetType(Class<?> targetType) {
      this.targetType = targetType;
    }

    public boolean isCollection() {
      return isCollection;
    }

    public void setCollection(boolean collection) {
      isCollection = collection;
    }

    public boolean isRelation() {
      return isRelation;
    }

    public void setRelation(boolean relation) {
      isRelation = relation;
    }

    @Nullable
    public RelationType getRelationType() {
      return relationType;
    }

    public void setRelationType(RelationType relationType) {
      this.relationType = relationType;
    }

    public boolean isEmbedded() {
      return isEmbedded;
    }

    public void setEmbedded(boolean embedded) {
      isEmbedded = embedded;
    }

    @Nullable
    public FieldCategory getFieldCategory() {
      return fieldCategory;
    }

    public void setFieldCategory(FieldCategory fieldCategory) {
      this.fieldCategory = fieldCategory;
    }

    @Nullable
    public List<String> getEnumValues() {
      return enumValues;
    }

    public void setEnumValues(List<String> enumValues) {
      this.enumValues = enumValues;
    }

    public String getSimpleName() {
      return path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
    }

  }

  public enum RelationType {
    TO_ONE, TO_MANY
  }

  public enum FieldCategory {
    PRIMITIVE, STRING, DATE, ENUM, COLLECTION, MAP, OBJECT
  }

}
