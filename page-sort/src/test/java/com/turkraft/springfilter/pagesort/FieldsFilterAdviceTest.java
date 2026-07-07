package com.turkraft.springfilter.pagesort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.turkraft.springfilter.boot.Fields;
import com.turkraft.springfilter.boot.FieldsFilterAdvice;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class FieldsFilterAdviceTest {

  private FieldsFilterAdvice advice;
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    advice = new FieldsFilterAdvice();
    request = mock(HttpServletRequest.class);
  }

  @AfterEach
  void tearDown() {
    FieldsFilterContext.clear();
  }

  @Test
  void testSupportsWithFieldsAnnotationAndParameter()
      throws Exception {
    Method method = TestController.class.getMethod("withFields");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("fields")).thenReturn("id,name");

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      boolean supports = advice.supports(returnType, null);
      assertTrue(supports);
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  void testSupportsWithFieldsAnnotationButNoParameter()
      throws Exception {
    Method method = TestController.class.getMethod("withFields");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("fields")).thenReturn(null);

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      boolean supports = advice.supports(returnType, null);
      assertFalse(supports);
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  void testSupportsWithDefaultValue()
      throws Exception {
    Method method = TestController.class.getMethod("withFieldsDefault");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("fields")).thenReturn(null);

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      boolean supports = advice.supports(returnType, null);
      assertTrue(supports);
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  void testSupportsWithoutFieldsAnnotation()
      throws Exception {
    Method method = TestController.class.getMethod("withoutFields");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("fields")).thenReturn("id,name");

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      boolean supports = advice.supports(returnType, null);
      assertFalse(supports);
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  void testSupportsWithNoRequestAttributes()
      throws Exception {
    Method method = TestController.class.getMethod("withFields");
    MethodParameter returnType = new MethodParameter(method, -1);

    RequestContextHolder.setRequestAttributes(null);

    boolean supports = advice.supports(returnType, null);
    assertFalse(supports);
  }

  @Test
  void testBeforeBodyWriteSetsFilterContext()
      throws Exception {
    Method method = TestController.class.getMethod("withFields");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("fields")).thenReturn("id,name");

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      TestObject body = new TestObject();
      Object result = advice.beforeBodyWrite(body, returnType, null, null, null, null);

      assertEquals(body, result);
      assertNotNull(FieldsFilterContext.get());
      assertTrue(FieldsFilterContext.get().getIncludePatterns().contains("id"));
      assertTrue(FieldsFilterContext.get().getIncludePatterns().contains("name"));
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  void testBeforeBodyWriteWithEmptyParameter()
      throws Exception {
    Method method = TestController.class.getMethod("withFields");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("fields")).thenReturn("");

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      TestObject body = new TestObject();
      Object result = advice.beforeBodyWrite(body, returnType, null, null, null, null);

      assertEquals(body, result);
      assertNull(FieldsFilterContext.get());
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  void testBeforeBodyWriteWithDefaultValue()
      throws Exception {
    Method method = TestController.class.getMethod("withFieldsDefault");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("fields")).thenReturn(null);

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      TestObject body = new TestObject();
      Object result = advice.beforeBodyWrite(body, returnType, null, null, null, null);

      assertEquals(body, result);
      assertNotNull(FieldsFilterContext.get());
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  void testBeforeBodyWriteWithNoRequestAttributes()
      throws Exception {
    Method method = TestController.class.getMethod("withFields");
    MethodParameter returnType = new MethodParameter(method, -1);

    RequestContextHolder.setRequestAttributes(null);

    TestObject body = new TestObject();
    Object result = advice.beforeBodyWrite(body, returnType, null, null, null, null);

    assertEquals(body, result);
  }

  @Test
  void testBeforeBodyWriteWithCustomParameter()
      throws Exception {
    Method method = TestController.class.getMethod("withCustomParameter");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("select")).thenReturn("id,name");

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      TestObject body = new TestObject();
      Object result = advice.beforeBodyWrite(body, returnType, null, null, null, null);

      assertEquals(body, result);
      assertNotNull(FieldsFilterContext.get());
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  void testSupportsReturnsTrueWhenRequiredEvenIfMissing()
      throws Exception {
    Method method = TestController.class.getMethod("withFieldsRequired");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("fields")).thenReturn(null);

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      boolean supports = advice.supports(returnType, null);
      assertTrue(supports);
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @Test
  void testBeforeBodyWriteThrowsWhenRequiredParameterMissing()
      throws Exception {
    Method method = TestController.class.getMethod("withFieldsRequired");
    MethodParameter returnType = new MethodParameter(method, -1);

    when(request.getParameter("fields")).thenReturn(null);

    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    try {
      TestObject body = new TestObject();
      assertThrows(IllegalArgumentException.class,
          () -> advice.beforeBodyWrite(body, returnType, null, null, null, null));
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  @RestController
  static class TestController {

    @GetMapping("/fields")
    @Fields
    public TestObject withFields() {
      return new TestObject();
    }

    @GetMapping("/fields-default")
    @Fields(defaultValue = "id,name")
    public TestObject withFieldsDefault() {
      return new TestObject();
    }

    @GetMapping("/custom")
    @Fields(parameter = "select")
    public TestObject withCustomParameter() {
      return new TestObject();
    }

    @GetMapping("/fields-required")
    @Fields(required = true)
    public TestObject withFieldsRequired() {
      return new TestObject();
    }

    @GetMapping("/no-fields")
    public TestObject withoutFields() {
      return new TestObject();
    }

  }

  static class TestObject {

    private final String id = "123";
    private final String name = "Test";
    private final String password = "secret";

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getPassword() {
      return password;
    }

  }

}
