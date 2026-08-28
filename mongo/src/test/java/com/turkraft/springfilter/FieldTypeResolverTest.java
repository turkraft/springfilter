package com.turkraft.springfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.turkraft.springfilter.converter.StringCustomObjectIdConverter.CustomObjectId;
import com.turkraft.springfilter.converter.StringCustomUUIDConverter.CustomUUID;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
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

  @Test
  void resolvesDbRefIdSegmentToReferencedIdType() {
    assertEquals(CustomObjectId.class, resolve("manager.$id"));
    assertEquals(CustomObjectId.class, resolve("roles.$id"));
  }

  @Test
  void resolvesDbRefRefAndDbSegmentsToString() {
    assertEquals(String.class, resolve("manager.$ref"));
    assertEquals(String.class, resolve("roles.$ref"));
    assertEquals(String.class, resolve("manager.$db"));
    assertEquals(String.class, resolve("roles.$db"));
  }

  @Test
  void resolvesUnknownDbRefSegmentToObject() {
    assertEquals(Object.class, resolve("manager.$unknown"));
  }

  @Test
  void resolvesDollarSegmentAfterEmbeddedFieldAsObject() {
    assertEquals(Object.class, resolve("nested.$id"));
  }

  @Test
  void resolvesDollarSegmentAsPlainMapKey() {
    assertEquals(String.class, resolve("metadata.$id"));
    assertEquals(Integer.class, resolve("counters.$id"));
  }

  @Test
  void resolvesDbRefIdSegmentToReferencedIdTypeVariants() {
    assertEquals(CustomObjectId.class,
        fieldTypeResolver.resolve(ObjectIdIdHolder.class, "ref.$id"));
    assertEquals(CustomUUID.class,
        fieldTypeResolver.resolve(UuidIdHolder.class, "ref.$id"));
    assertEquals(Integer.class,
        fieldTypeResolver.resolve(IntegerIdHolder.class, "ref.$id"));
    assertEquals(CustomObjectId.class,
        fieldTypeResolver.resolve(NoIdHolder.class, "ref.$id"));
    assertEquals(CustomObjectId.class,
        fieldTypeResolver.resolve(UntypedListHolder.class, "refs.$id"));
  }

  @Test
  void isCollectionDbRefFieldDetectsOnlyCollections() {
    assertTrue(fieldTypeResolver.isCollectionDbRefField(TestEntity.class, "roles.$id"));
    assertFalse(fieldTypeResolver.isCollectionDbRefField(TestEntity.class, "manager.$id"));
    assertFalse(fieldTypeResolver.isCollectionDbRefField(TestEntity.class, "metadata.$id"));
    assertFalse(fieldTypeResolver.isCollectionDbRefField(TestEntity.class, "roles"));
    assertFalse(fieldTypeResolver.isCollectionDbRefField(TestEntity.class, "unknown.$id"));
  }

  @Test
  void hasDollarSegmentDetectsDollarSegments() {
    assertTrue(fieldTypeResolver.hasDollarSegment("role.$id"));
    assertTrue(fieldTypeResolver.hasDollarSegment("$id"));
    assertTrue(fieldTypeResolver.hasDollarSegment("a.b.$ref"));
    assertFalse(fieldTypeResolver.hasDollarSegment("role"));
    assertFalse(fieldTypeResolver.hasDollarSegment("nested.field"));
    assertFalse(fieldTypeResolver.hasDollarSegment("a$b"));
  }

  @Test
  void getFieldReturnsNullForDbRefSegments() {
    assertNull(fieldTypeResolver.getField(TestEntity.class, "roles.$id"));
    assertNull(fieldTypeResolver.getField(TestEntity.class, "manager.$id"));
  }

  @Test
  void resolvesDocumentReferenceIdSegmentToReferencedIdType() {
    assertEquals(CustomObjectId.class, resolve("advisor.$id"));
    assertEquals(CustomObjectId.class, resolve("teams.$id"));
  }

  @Test
  void resolvesDocumentReferenceNonIdSegmentsToObject() {
    assertEquals(Object.class, resolve("advisor.$ref"));
    assertEquals(Object.class, resolve("advisor.$db"));
    assertEquals(Object.class, resolve("advisor.$unknown"));
    assertEquals(String.class, resolve("manager.$ref"));
  }

  @Test
  void isCollectionDbRefFieldDetectsDocumentReferenceCollections() {
    assertTrue(fieldTypeResolver.isCollectionDbRefField(TestEntity.class, "teams.$id"));
    assertFalse(fieldTypeResolver.isCollectionDbRefField(TestEntity.class, "advisor.$id"));
  }

  @Test
  void storedFieldPathKeepsDbRefSegmentButDropsDocumentReferenceId() {
    assertEquals("roles.$id", fieldTypeResolver.storedFieldPath(TestEntity.class, "roles.$id"));
    assertEquals("manager.$ref",
        fieldTypeResolver.storedFieldPath(TestEntity.class, "manager.$ref"));
    assertEquals("advisor", fieldTypeResolver.storedFieldPath(TestEntity.class, "advisor.$id"));
    assertEquals("teams", fieldTypeResolver.storedFieldPath(TestEntity.class, "teams.$id"));
    assertEquals("advisor.$ref",
        fieldTypeResolver.storedFieldPath(TestEntity.class, "advisor.$ref"));
    assertEquals("nested.field",
        fieldTypeResolver.storedFieldPath(TestEntity.class, "nested.field"));
    assertEquals("$id", fieldTypeResolver.storedFieldPath(TestEntity.class, "$id"));
  }

  static class GenericEntity<T> {

    private Map<String, T> genericMap;

  }

  static class ObjectIdIdHolder {

    @DBRef
    private ObjectIdIdEntity ref;

  }

  static class ObjectIdIdEntity {

    @Id
    private org.bson.types.ObjectId id;

  }

  static class UuidIdHolder {

    @DBRef
    private UuidIdEntity ref;

  }

  static class UuidIdEntity {

    @Id
    private UUID id;

  }

  static class IntegerIdHolder {

    @DBRef
    private IntegerIdEntity ref;

  }

  static class IntegerIdEntity {

    @Id
    private Integer id;

  }

  static class NoIdHolder {

    @DBRef
    private PlainEntity ref;

  }

  static class PlainEntity {

    private String name;

  }

  static class UntypedListHolder {

    @DBRef
    private List refs;

  }

}
