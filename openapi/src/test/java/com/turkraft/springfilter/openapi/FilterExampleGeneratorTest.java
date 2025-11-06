package com.turkraft.springfilter.openapi;

import com.turkraft.springfilter.openapi.generator.FilterExampleGenerator;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterExampleGeneratorTest {

  private FilterExampleGenerator generator;

  @BeforeEach
  void setUp() {
    EntityIntrospector introspector = new EntityIntrospector();
    generator = new FilterExampleGenerator(introspector);
  }

  @Test
  void shouldGenerateExamples() {
    List<String> examples = generator.generateExamples(TestEntity.class);

    assertNotNull(examples);
    assertFalse(examples.isEmpty());
  }

  @Test
  void shouldGenerateNumericExample() {
    List<String> examples = generator.generateExamples(TestEntity.class);

    boolean hasNumericExample = examples.stream()
        .anyMatch(ex -> ex.contains(">") && ex.contains("id"));

    assertTrue(hasNumericExample, "Should have numeric comparison example");
  }

  @Test
  void shouldGenerateStringExample() {
    List<String> examples = generator.generateExamples(TestEntity.class);

    boolean hasStringExample = examples.stream()
        .anyMatch(ex -> ex.contains("~") && ex.contains("name"));

    assertTrue(hasStringExample, "Should have string pattern example");
  }

  @Test
  void shouldGenerateDateExample() {
    List<String> examples = generator.generateExamples(TestEntity.class);

    boolean hasDateExample = examples.stream()
        .anyMatch(ex -> ex.contains("createdAt"));

    assertTrue(hasDateExample, "Should have date example");
  }

  @Test
  void shouldGenerateEnumExample() {
    // Use a simpler entity with enum as first field
    List<String> examples = generator.generateExamples(SimpleEnumEntity.class);

    boolean hasEnumExample = examples.stream()
        .anyMatch(ex -> ex.contains("status") && ex.contains("ACTIVE"));

    assertTrue(hasEnumExample, "Should have enum example");
  }

  @Test
  void shouldGenerateCollectionExample() {
    List<String> examples = generator.generateExamples(TestEntity.class);

    boolean hasCollectionExample = examples.stream()
        .anyMatch(ex -> ex.contains("size(") && ex.contains("tags"));

    assertTrue(hasCollectionExample, "Should have collection size example");
  }

  @Test
  void shouldGenerateComprehensiveExample() {
    String example = generator.generateComprehensiveExample(TestEntity.class);

    assertNotNull(example);
    assertFalse(example.isEmpty());
    assertTrue(example.length() > 10, "Comprehensive example should be substantial");
  }

  @Test
  void shouldLimitExampleCount() {
    List<String> examples = generator.generateExamples(TestEntity.class);

    assertTrue(examples.size() <= 10, "Should limit to reasonable number of examples");
  }

  // Test entity
  static class TestEntity {
    private Long id;
    private String name;
    private LocalDate createdAt;
    private Status status;
    private List<String> tags;

    enum Status {
      ACTIVE, INACTIVE, PENDING
    }
  }

  // Simple entity with enum first
  static class SimpleEnumEntity {
    private Status status;

    enum Status {
      ACTIVE, INACTIVE, PENDING
    }
  }

}
