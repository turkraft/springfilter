package com.turkraft.springfilter.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.turkraft.springfilter.boot.Fields;
import com.turkraft.springfilter.boot.Pagination;
import com.turkraft.springfilter.boot.Sort;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector;
import com.turkraft.springfilter.openapi.springdoc.PageSortParameterCustomizer;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;

class PageSortParameterCustomizerTest {

  private PageSortParameterCustomizer customizer;

  @BeforeEach
  void setUp() {
    EntityIntrospector entityIntrospector = new EntityIntrospector();
    customizer = new PageSortParameterCustomizer(entityIntrospector);
  }

  @Test
  void shouldCustomizeSortParameter() throws Exception {
    TestController controller = new TestController();
    Method method = TestController.class.getMethod("withSort", org.springframework.data.domain.Sort.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Operation operation = new Operation();
    Operation result = customizer.customize(operation, handlerMethod);

    assertNotNull(result);
    assertNotNull(result.getParameters());
    assertEquals(1, result
        .getParameters()
        .size());

    Parameter param = result
        .getParameters()
        .get(0);
    assertEquals("sort", param.getName());
    assertEquals("query", param.getIn());
    assertFalse(param.getRequired());
    assertTrue(param
        .getDescription()
        .contains("prefix for descending"));
    assertTrue(param
        .getDescription()
        .contains("Example"));
  }

  @Test
  void shouldCustomizeSortParameterWithAnnotation() throws Exception {
    TestController controller = new TestController();
    Method method = TestController.class.getMethod("withSortAnnotation", org.springframework.data.domain.Sort.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Operation operation = new Operation();
    Operation result = customizer.customize(operation, handlerMethod);

    assertNotNull(result);
    assertNotNull(result.getParameters());

    Optional<Parameter> sortParam = result
        .getParameters()
        .stream()
        .filter(p -> "customSort".equals(p.getName()))
        .findFirst();

    assertTrue(sortParam.isPresent());
    assertEquals("customSort", sortParam
        .get()
        .getName());
    assertTrue(sortParam
        .get()
        .getRequired());
  }

  @Test
  void shouldCustomizePageParameter() throws Exception {
    TestController controller = new TestController();
    Method method = TestController.class.getMethod("withPage", Pageable.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Operation operation = new Operation();
    Operation result = customizer.customize(operation, handlerMethod);

    assertNotNull(result);
    assertNotNull(result.getParameters());
    assertEquals(3, result
        .getParameters()
        .size());

    Optional<Parameter> pageParam = result
        .getParameters()
        .stream()
        .filter(p -> "page".equals(p.getName()))
        .findFirst();

    Optional<Parameter> sizeParam = result
        .getParameters()
        .stream()
        .filter(p -> "size".equals(p.getName()))
        .findFirst();

    Optional<Parameter> sortParam = result
        .getParameters()
        .stream()
        .filter(p -> "sort".equals(p.getName()))
        .findFirst();

    assertTrue(pageParam.isPresent());
    assertTrue(sizeParam.isPresent());
    assertTrue(sortParam.isPresent());

    assertEquals("query", pageParam
        .get()
        .getIn());
    assertEquals("integer", pageParam
        .get()
        .getSchema()
        .getType());
    assertEquals(BigDecimal.ZERO, pageParam
        .get()
        .getSchema()
        .getMinimum());

    assertEquals("query", sizeParam
        .get()
        .getIn());
    assertEquals("integer", sizeParam
        .get()
        .getSchema()
        .getType());
    assertTrue(sizeParam
        .get()
        .getDescription()
        .contains("max"));

    assertEquals("query", sortParam
        .get()
        .getIn());
    assertEquals("string", sortParam
        .get()
        .getSchema()
        .getType());
    assertTrue(sortParam
        .get()
        .getDescription()
        .contains("prefix for descending"));
  }

  @Test
  void shouldCustomizePageParameterWithAnnotation() throws Exception {
    TestController controller = new TestController();
    Method method = TestController.class.getMethod("withPageAnnotation", Pageable.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Operation operation = new Operation();
    Operation result = customizer.customize(operation, handlerMethod);

    assertNotNull(result);
    assertNotNull(result.getParameters());

    Optional<Parameter> pageParam = result
        .getParameters()
        .stream()
        .filter(p -> "p".equals(p.getName()))
        .findFirst();

    Optional<Parameter> sizeParam = result
        .getParameters()
        .stream()
        .filter(p -> "s".equals(p.getName()))
        .findFirst();

    Optional<Parameter> sortParam = result
        .getParameters()
        .stream()
        .filter(p -> "order".equals(p.getName()))
        .findFirst();

    assertTrue(pageParam.isPresent());
    assertTrue(sizeParam.isPresent());
    assertTrue(sortParam.isPresent());

    assertEquals(1, pageParam
        .get()
        .getSchema()
        .getDefault());
    assertEquals(50, sizeParam
        .get()
        .getSchema()
        .getDefault());
    assertEquals(BigDecimal.valueOf(200), sizeParam
        .get()
        .getSchema()
        .getMaximum());
  }

  @Test
  void shouldCustomizeFieldsParameter() throws Exception {
    TestController controller = new TestController();
    Method method = TestController.class.getMethod("withFields");
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Operation operation = new Operation();
    Operation result = customizer.customize(operation, handlerMethod);

    assertNotNull(result);
    assertNotNull(result.getParameters());
    assertEquals(1, result
        .getParameters()
        .size());

    Parameter param = result
        .getParameters()
        .get(0);
    assertEquals("fields", param.getName());
    assertEquals("query", param.getIn());
    assertFalse(param.getRequired());
    assertTrue(param
        .getDescription()
        .contains("include in response"));
    assertTrue(param
        .getDescription()
        .contains("wildcard"));
  }

  @Test
  void shouldCustomizeFieldsParameterWithAnnotation() throws Exception {
    TestController controller = new TestController();
    Method method = TestController.class.getMethod("withFieldsAnnotation");
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Operation operation = new Operation();
    Operation result = customizer.customize(operation, handlerMethod);

    assertNotNull(result);
    assertNotNull(result.getParameters());

    Optional<Parameter> fieldsParam = result
        .getParameters()
        .stream()
        .filter(p -> "select".equals(p.getName()))
        .findFirst();

    assertTrue(fieldsParam.isPresent());
    assertEquals("select", fieldsParam
        .get()
        .getName());
    assertTrue(fieldsParam
        .get()
        .getRequired());
    assertEquals("id,name", fieldsParam
        .get()
        .getSchema()
        .getDefault());
  }

  @Test
  void shouldCustomizeAllParametersTogether() throws Exception {
    TestController controller = new TestController();
    Method method = TestController.class.getMethod("withAllAnnotations",
        org.springframework.data.domain.Sort.class, Pageable.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Operation operation = new Operation();
    Operation result = customizer.customize(operation, handlerMethod);

    assertNotNull(result);
    assertNotNull(result.getParameters());
    assertEquals(4, result
        .getParameters()
        .size());

    assertTrue(result
        .getParameters()
        .stream()
        .anyMatch(p -> "sort".equals(p.getName())));
    assertTrue(result
        .getParameters()
        .stream()
        .anyMatch(p -> "page".equals(p.getName())));
    assertTrue(result
        .getParameters()
        .stream()
        .anyMatch(p -> "size".equals(p.getName())));
    assertTrue(result
        .getParameters()
        .stream()
        .anyMatch(p -> "fields".equals(p.getName())));
  }

  @Test
  void shouldNotIncludeSortParameterWhenDisabled() throws Exception {
    TestController controller = new TestController();
    Method method = TestController.class.getMethod("withPageNoSort", Pageable.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Operation operation = new Operation();
    Operation result = customizer.customize(operation, handlerMethod);

    assertNotNull(result);
    assertNotNull(result.getParameters());
    assertEquals(2, result.getParameters().size());

    assertTrue(result.getParameters().stream().anyMatch(p -> "page".equals(p.getName())));
    assertTrue(result.getParameters().stream().anyMatch(p -> "size".equals(p.getName())));
    assertFalse(result.getParameters().stream().anyMatch(p -> "sort".equals(p.getName())));
  }

  @Test
  void shouldHandleNullOperation() throws Exception {
    TestController controller = new TestController();
    Method method = TestController.class.getMethod("withSort", org.springframework.data.domain.Sort.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    Operation result = customizer.customize(null, handlerMethod);

    assertEquals(null, result);
  }

  @Test
  void shouldHandleNullHandlerMethod() {
    Operation operation = new Operation();
    Operation result = customizer.customize(operation, null);

    assertEquals(operation, result);
  }

  @RestController
  static class TestController {

    @GetMapping("/sort")
    public String withSort(@Sort org.springframework.data.domain.Sort sort) {
      return "ok";
    }

    @GetMapping("/sort-annotation")
    public String withSortAnnotation(@Sort(parameter = "customSort", required = true) org.springframework.data.domain.Sort sort) {
      return "ok";
    }

    @GetMapping("/page")
    public String withPage(@Pagination Pageable page) {
      return "ok";
    }

    @GetMapping("/page-annotation")
    public String withPageAnnotation(
        @Pagination(pageParameter = "p", sizeParameter = "s", sortParameter = "order", defaultPage = 1, defaultSize = 50, maxSize = 200) Pageable page) {
      return "ok";
    }

    @GetMapping("/page-no-sort")
    public String withPageNoSort(@Pagination(enableSort = false) Pageable page) {
      return "ok";
    }

    @Fields
    @GetMapping("/fields")
    public String withFields() {
      return "ok";
    }

    @Fields(parameter = "select", required = true, defaultValue = "id,name")
    @GetMapping("/fields-annotation")
    public String withFieldsAnnotation() {
      return "ok";
    }

    @Fields
    @GetMapping("/all")
    public String withAllAnnotations(
        @Sort org.springframework.data.domain.Sort sort,
        @Pagination Pageable page) {
      return "ok";
    }

  }

}
