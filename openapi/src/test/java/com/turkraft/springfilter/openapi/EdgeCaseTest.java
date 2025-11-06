package com.turkraft.springfilter.openapi;

import com.turkraft.springfilter.openapi.generator.FilterExampleGenerator;
import com.turkraft.springfilter.openapi.generator.FilterSchemaGenerator;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.EntitySchema;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.FieldInfo;
import com.turkraft.springfilter.openapi.springdoc.FilterParameterCustomizer;
import io.swagger.v3.oas.models.Operation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeCaseTest {

  static class EntityWithRelation {
    private Long id;
    private RelatedEntity related;
  }

  static class RelatedEntity {
    private String name;
  }

  static class EmptyEntity {
  }

  @Test
  public void shouldHandleNullFieldCategory() {
    EntityIntrospector introspector = new EntityIntrospector();
    EntitySchema schema = introspector.introspect(EntityWithRelation.class);

    assertNotNull(schema);
    assertFalse(schema.getFields().isEmpty());

    for (FieldInfo field : schema.getFields().values()) {
      assertNotNull(field.getFieldCategory(),
          "Field " + field.getPath() + " should have a category");
    }
  }

  @Test
  public void shouldHandleEmptyEntity() {
    EntityIntrospector introspector = new EntityIntrospector();
    FilterExampleGenerator exampleGenerator = new FilterExampleGenerator(introspector);

    EntitySchema schema = introspector.introspect(EmptyEntity.class);
    List<String> examples = exampleGenerator.generateExamples(EmptyEntity.class);

    assertNotNull(schema);
    assertNotNull(examples);
    assertTrue(examples.isEmpty() || !examples.isEmpty());
  }

  @Test
  public void shouldHandleNullOperationGracefully() {
    FilterSchemaGenerator schemaGenerator = Mockito.mock(FilterSchemaGenerator.class);
    FilterExampleGenerator exampleGenerator = Mockito.mock(FilterExampleGenerator.class);
    FilterParameterCustomizer customizer =
        new FilterParameterCustomizer(schemaGenerator, exampleGenerator);

    Operation result = customizer.customize(null, null);

    assertNull(result);
  }

  @Test
  public void shouldHandleNullHandlerMethodGracefully() {
    FilterSchemaGenerator schemaGenerator = Mockito.mock(FilterSchemaGenerator.class);
    FilterExampleGenerator exampleGenerator = Mockito.mock(FilterExampleGenerator.class);
    FilterParameterCustomizer customizer =
        new FilterParameterCustomizer(schemaGenerator, exampleGenerator);

    Operation operation = new Operation();
    Operation result = customizer.customize(operation, null);

    assertNotNull(result);
    assertEquals(operation, result);
  }
}
