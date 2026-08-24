package com.turkraft.springfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.turkraft.springfilter.converter.StringCustomUUIDConverter.CustomUUID;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FieldTypeResolverTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private FieldTypeResolver fieldTypeResolver;

  private Class<?> resolve(String path) {
    return fieldTypeResolver.resolve(TestEntity.class, path);
  }

  @Test
  void resolvesPlainFields() {
    assertEquals(String.class, resolve("string"));
    assertEquals(int.class, resolve("integer"));
    assertEquals(Integer.class, resolve("integers"));
    assertEquals(NestedTestEntity.class, resolve("nested"));
    assertEquals(String.class, resolve("nested.field"));
  }

  @Test
  void resolvesUnknownFieldsAsObject() {
    assertEquals(Object.class, resolve("unknown"));
    assertEquals(Object.class, resolve("nested.unknown"));
  }

  @Test
  void resolvesMapFieldItself() {
    assertEquals(Map.class, resolve("metadata"));
  }

  @Test
  void resolvesMapValueType() {
    assertEquals(String.class, resolve("metadata.someKey"));
  }

  @Test
  void resolvesIntegerMapValueType() {
    assertEquals(Integer.class, resolve("counters.views"));
  }

  @Test
  void resolvesObjectMapValueType() {
    assertEquals(NestedTestEntity.class, resolve("nestedByName.alice"));
    assertEquals(String.class, resolve("nestedByName.alice.field"));
  }

  @Test
  void resolvesNestedMapValueTypes() {
    assertEquals(Map.class, resolve("nestedMaps.outer"));
    assertEquals(String.class, resolve("nestedMaps.outer.inner"));
  }

  @Test
  void resolvesRawMapValueAsObject() {
    assertEquals(Object.class, resolve("rawMap.anything"));
    assertEquals(Object.class, resolve("rawMap.anything.deeper"));
  }

  @Test
  void resolvesUuidMapValueAsCustomUUID() {
    assertEquals(CustomUUID.class, resolve("uuidByName.foo"));
  }

  @Test
  void resolvesPlainUuidListAsUuidNotCustomUUID() {
    assertEquals(UUID.class, resolve("uuidList"));
  }

  @Test
  void resolvesMapOfListValueToElementType() {
    assertEquals(String.class, resolve("tags.someKey"));
    assertEquals(Integer.class, resolve("intLists.someKey"));
  }

  @Test
  void resolvesMapOfListValueToElementGenericTypes() {
    assertEquals(UUID.class, resolve("uuidListByName.someKey"));
  }

  @Test
  void resolvesMapOfUuidArrayValueToUuid() {
    assertEquals(UUID.class, resolve("uuidArrays.someKey"));
  }

  @Test
  void resolvesEnumMapValue() {
    assertEquals(TestEntity.Status.class, resolve("statuses.someKey"));
  }

  @Test
  void resolvesBooleanMapValue() {
    assertEquals(Boolean.class, resolve("flags.someKey"));
  }

  @Test
  void resolvesUnknownSegmentAfterMapValueAsObject() {
    assertEquals(Object.class, resolve("metadata.foo.bar"));
    assertEquals(Object.class, resolve("counters.views.bar"));
  }

  @Test
  void resolvesMapKeyThatCollidesWithValueClassFieldName() {
    assertEquals(NestedTestEntity.class, resolve("nestedByName.field"));
    assertEquals(String.class, resolve("nestedByName.field.field"));
  }

  @Test
  void getFieldReturnsMapField() {
    assertNotNull(fieldTypeResolver.getField(TestEntity.class, "metadata"));
    assertEquals("metadata", fieldTypeResolver.getField(TestEntity.class, "metadata").getName());
  }

  @Test
  void getFieldReturnsNullForMapKey() {
    assertNull(fieldTypeResolver.getField(TestEntity.class, "metadata.someKey"));
    assertNull(fieldTypeResolver.getField(TestEntity.class, "nestedByName.alice.field"));
  }

  @Test
  void hashMapKeysAreNotJavaFields() {
    assertEquals(String.class, resolve("hashMapWebsites.size"));
    assertEquals(String.class, resolve("hashMapWebsites.keySet"));
    assertEquals(String.class, resolve("hashMapWebsites.values"));
    assertEquals(HashMap.class, resolve("hashMapWebsites"));
    assertNull(fieldTypeResolver.getField(TestEntity.class, "hashMapWebsites.size"));
    assertNull(fieldTypeResolver.getField(TestEntity.class, "hashMapWebsites.keySet"));
    assertNotNull(fieldTypeResolver.getField(TestEntity.class, "hashMapWebsites"));
  }

  @Test
  void wildcardMapValueResolvesToObject() {
    assertEquals(Object.class, resolve("wildcardMap.anything"));
  }

  @Test
  void typeVariableMapValueResolvesToObject() {
    GenericEntity<String> entity = new GenericEntity<>();
    assertEquals(Object.class,
        fieldTypeResolver.resolve(entity.getClass(), "genericMap.someKey"));
  }

  static class GenericEntity<T> {

    private Map<String, T> genericMap;

  }

}
