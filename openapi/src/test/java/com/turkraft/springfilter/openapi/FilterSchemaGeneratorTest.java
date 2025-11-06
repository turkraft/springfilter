package com.turkraft.springfilter.openapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.turkraft.springfilter.definition.FilterFunction;
import com.turkraft.springfilter.definition.FilterFunctions;
import com.turkraft.springfilter.definition.FilterInfixOperator;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.definition.FilterPlaceholders;
import com.turkraft.springfilter.openapi.documentation.FunctionDocumentationProvider;
import com.turkraft.springfilter.openapi.documentation.OperatorDocumentationProvider;
import com.turkraft.springfilter.openapi.documentation.PlaceholderDocumentationProvider;
import com.turkraft.springfilter.openapi.generator.FilterSchemaGenerator;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilterSchemaGeneratorTest {

  private FilterSchemaGenerator generator;

  @BeforeEach
  void setUp() {
    EntityIntrospector introspector = new EntityIntrospector();

    // Mock operators
    FilterOperators operators = mock(FilterOperators.class);
    when(operators.getInfixOperators()).thenReturn(Collections.emptyList());
    when(operators.getPrefixOperators()).thenReturn(Collections.emptyList());
    when(operators.getPostfixOperators()).thenReturn(Collections.emptyList());

    // Mock functions
    FilterFunctions functions = mock(FilterFunctions.class);
    when(functions.getFunctions()).thenReturn(Collections.emptyList());

    // Mock placeholders
    FilterPlaceholders placeholders = mock(FilterPlaceholders.class);
    when(placeholders.getPlaceholders()).thenReturn(Collections.emptyList());

    OperatorDocumentationProvider operatorProvider =
        new OperatorDocumentationProvider(operators);
    FunctionDocumentationProvider functionProvider =
        new FunctionDocumentationProvider(functions);
    PlaceholderDocumentationProvider placeholderProvider =
        new PlaceholderDocumentationProvider(placeholders);

    generator = new FilterSchemaGenerator(introspector, operatorProvider, functionProvider, placeholderProvider);
  }

  @Test
  void shouldGenerateSchemaDescription() {
    String schema = generator.generateSchemaDescription(TestEntity.class);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());
  }

  @Test
  void shouldIncludeFieldsSection() {
    String schema = generator.generateSchemaDescription(TestEntity.class);

    assertTrue(schema.contains("Available Fields"));
    assertTrue(schema.contains("id"));
    assertTrue(schema.contains("name"));
  }

  @Test
  void shouldIncludeFieldTypes() {
    String schema = generator.generateSchemaDescription(TestEntity.class);

    assertTrue(schema.contains("integer") || schema.contains("number"));
    assertTrue(schema.contains("string"));
    assertTrue(schema.contains("date"));
  }

  @Test
  void shouldNotIncludeOperatorsSectionWhenEmpty() {
    String schema = generator.generateSchemaDescription(TestEntity.class);

    assertFalse(schema.contains("Operators"));
  }

  @Test
  void shouldNotIncludeFunctionsSectionWhenEmpty() {
    String schema = generator.generateSchemaDescription(TestEntity.class);

    assertFalse(schema.contains("Functions"));
  }

  @Test
  void shouldIncludeOperatorsSectionWhenPresent() {
    // Create a mock operator
    FilterInfixOperator mockOperator = mock(FilterInfixOperator.class);
    when(mockOperator.getTokens()).thenReturn(new String[]{":"});
    when(mockOperator.getDescription()).thenReturn(null);
    when(mockOperator.getExample()).thenReturn(null);
    when(mockOperator.getPriority()).thenReturn(5);

    // Setup operators mock with one operator
    FilterOperators operators = mock(FilterOperators.class);
    when(operators.getInfixOperators()).thenReturn(List.of(mockOperator));
    when(operators.getPrefixOperators()).thenReturn(Collections.emptyList());
    when(operators.getPostfixOperators()).thenReturn(Collections.emptyList());

    FilterFunctions functions = mock(FilterFunctions.class);
    when(functions.getFunctions()).thenReturn(Collections.emptyList());

    // Mock placeholders
    FilterPlaceholders placeholders = mock(FilterPlaceholders.class);
    when(placeholders.getPlaceholders()).thenReturn(Collections.emptyList());

    OperatorDocumentationProvider operatorProvider =
        new OperatorDocumentationProvider(operators);
    FunctionDocumentationProvider functionProvider =
        new FunctionDocumentationProvider(functions);
    PlaceholderDocumentationProvider placeholderProvider =
        new PlaceholderDocumentationProvider(placeholders);

    EntityIntrospector introspector = new EntityIntrospector();
    FilterSchemaGenerator gen = new FilterSchemaGenerator(introspector, operatorProvider,
        functionProvider, placeholderProvider);

    String schema = gen.generateSchemaDescription(TestEntity.class);

    assertTrue(schema.contains("Operators"));
  }

  @Test
  void shouldIncludeFunctionsSectionWhenPresent() {
    // Setup mocks
    FilterOperators operators = mock(FilterOperators.class);
    when(operators.getInfixOperators()).thenReturn(Collections.emptyList());
    when(operators.getPrefixOperators()).thenReturn(Collections.emptyList());
    when(operators.getPostfixOperators()).thenReturn(Collections.emptyList());

    FilterFunction mockFunction = mock(FilterFunction.class);
    when(mockFunction.getName()).thenReturn("testFunc");
    when(mockFunction.getDescription()).thenReturn(null);
    when(mockFunction.getExample()).thenReturn(null);

    FilterFunctions functions = mock(FilterFunctions.class);
    when(functions.getFunctions()).thenReturn(List.of(mockFunction));

    FilterPlaceholders placeholders = mock(FilterPlaceholders.class);
    when(placeholders.getPlaceholders()).thenReturn(Collections.emptyList());

    OperatorDocumentationProvider operatorProvider =
        new OperatorDocumentationProvider(operators);
    FunctionDocumentationProvider functionProvider =
        new FunctionDocumentationProvider(functions);
    PlaceholderDocumentationProvider placeholderProvider =
        new PlaceholderDocumentationProvider(placeholders);

    EntityIntrospector introspector = new EntityIntrospector();
    FilterSchemaGenerator gen = new FilterSchemaGenerator(introspector, operatorProvider,
        functionProvider, placeholderProvider);

    String schema = gen.generateSchemaDescription(TestEntity.class);

    assertTrue(schema.contains("Functions"));
  }

  @Test
  void shouldGenerateShortDescription() {
    String shortDesc = generator.generateShortDescription(TestEntity.class);

    assertNotNull(shortDesc);
    assertTrue(shortDesc.contains("Filter"));
    assertTrue(shortDesc.contains("TestEntity"));
  }

  @Test
  void shouldGetAvailableFields() {
    List<String> fields = generator.getAvailableFields(TestEntity.class);

    assertNotNull(fields);
    assertTrue(fields.contains("id"));
    assertTrue(fields.contains("name"));
    assertTrue(fields.contains("createdAt"));
    assertTrue(fields.contains("status"));
    assertTrue(fields.contains("tags"));
  }

  @Test
  void shouldHandleEnumFields() {
    String schema = generator.generateSchemaDescription(TestEntity.class);

    assertTrue(schema.contains("enum"));
    assertTrue(schema.contains("status"));
  }

  @Test
  void shouldHandleCollectionFields() {
    String schema = generator.generateSchemaDescription(TestEntity.class);

    assertTrue(schema.contains("collection") || schema.contains("tags"));
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

}
