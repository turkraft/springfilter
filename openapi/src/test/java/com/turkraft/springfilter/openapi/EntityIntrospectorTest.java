package com.turkraft.springfilter.openapi;

import com.turkraft.springfilter.openapi.introspection.EntityIntrospector;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.EntitySchema;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.FieldCategory;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.FieldInfo;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityIntrospectorTest {

  private EntityIntrospector introspector;

  @BeforeEach
  void setUp() {
    introspector = new EntityIntrospector();
  }

  @Test
  void shouldIntrospectSimpleEntity() {
    EntitySchema schema = introspector.introspect(TestEntity.class);

    assertNotNull(schema);
    assertEquals(TestEntity.class, schema.getEntityClass());
    assertFalse(schema.getFields().isEmpty());
  }

  @Test
  void shouldDetectPrimitiveFields() {
    EntitySchema schema = introspector.introspect(TestEntity.class);

    FieldInfo idField = schema.getFields().get("id");
    assertNotNull(idField);
    assertEquals(FieldCategory.PRIMITIVE, idField.getFieldCategory());
    assertEquals(Long.class, idField.getType());
  }

  @Test
  void shouldDetectStringFields() {
    EntitySchema schema = introspector.introspect(TestEntity.class);

    FieldInfo nameField = schema.getFields().get("name");
    assertNotNull(nameField);
    assertEquals(FieldCategory.STRING, nameField.getFieldCategory());
  }

  @Test
  void shouldDetectDateFields() {
    EntitySchema schema = introspector.introspect(TestEntity.class);

    FieldInfo dateField = schema.getFields().get("createdAt");
    assertNotNull(dateField);
    assertEquals(FieldCategory.DATE, dateField.getFieldCategory());
  }

  @Test
  void shouldDetectEnumFields() {
    EntitySchema schema = introspector.introspect(TestEntity.class);

    FieldInfo statusField = schema.getFields().get("status");
    assertNotNull(statusField);
    assertEquals(FieldCategory.ENUM, statusField.getFieldCategory());
    assertNotNull(statusField.getEnumValues());
    assertTrue(statusField.getEnumValues().contains("ACTIVE"));
    assertTrue(statusField.getEnumValues().contains("INACTIVE"));
  }

  @Test
  void shouldDetectCollectionFields() {
    EntitySchema schema = introspector.introspect(TestEntity.class);

    FieldInfo tagsField = schema.getFields().get("tags");
    assertNotNull(tagsField);
    assertTrue(tagsField.isCollection());
    assertEquals(String.class, tagsField.getTargetType());
  }

  @Test
  void shouldCacheResults() {
    EntitySchema schema1 = introspector.introspect(TestEntity.class);
    EntitySchema schema2 = introspector.introspect(TestEntity.class);

    assertSame(schema1, schema2);
  }

  @Test
  void shouldClearCache() {
    EntitySchema schema1 = introspector.introspect(TestEntity.class);
    introspector.clearCache();
    EntitySchema schema2 = introspector.introspect(TestEntity.class);

    assertNotSame(schema1, schema2);
  }

  // Test entity
  static class TestEntity {

    private Long id;
    private String name;
    private LocalDate createdAt;
    private Status status;
    private List<String> tags;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public LocalDate getCreatedAt() {
      return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
      this.createdAt = createdAt;
    }

    public Status getStatus() {
      return status;
    }

    public void setStatus(Status status) {
      this.status = status;
    }

    public List<String> getTags() {
      return tags;
    }

    public void setTags(List<String> tags) {
      this.tags = tags;
    }

    enum Status {
      ACTIVE, INACTIVE, PENDING
    }
  }

}
