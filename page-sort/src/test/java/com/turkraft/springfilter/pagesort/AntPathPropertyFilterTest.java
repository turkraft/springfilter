package com.turkraft.springfilter.pagesort;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AntPathPropertyFilterTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  void testConstructorWithNullFieldsThrowsException() {
    assertThrows(NullPointerException.class, () -> new AntPathPropertyFilter(null));
  }

  @Test
  void testIncludeSingleField() throws JsonProcessingException {
    FieldsExpression fields = new FieldsExpression("name");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json = objectMapper.writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("John"));
    assertFalse(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testIncludeMultipleFields() throws JsonProcessingException {
    FieldsExpression fields = new FieldsExpression("name,email");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json = objectMapper.writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testExcludeField() throws JsonProcessingException {
    FieldsExpression fields = new FieldsExpression("-password");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json = objectMapper.writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testIncludeAllExcludeOne() throws JsonProcessingException {
    FieldsExpression fields = new FieldsExpression("*,-password");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json = objectMapper.writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testNestedFieldInclusion() throws JsonProcessingException {
    FieldsExpression fields = new FieldsExpression("user.name");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);
    objectMapper.addMixIn(TestOrder.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestOrder order = new TestOrder(123, user);
    String json = objectMapper.writeValueAsString(order);

    assertTrue(json.contains("user"));
    assertTrue(json.contains("name"));
    assertTrue(json.contains("John"));
    assertFalse(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testNestedFieldWildcard() throws JsonProcessingException {
    FieldsExpression fields = new FieldsExpression("user.*");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);
    objectMapper.addMixIn(TestOrder.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestOrder order = new TestOrder(123, user);
    String json = objectMapper.writeValueAsString(order);

    assertTrue(json.contains("user"));
    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertTrue(json.contains("password"));
    assertFalse(json.contains("\"id\""));
  }

  @Test
  void testExcludeNestedField() throws JsonProcessingException {
    FieldsExpression fields = new FieldsExpression("*,-user.password");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);
    objectMapper.addMixIn(TestOrder.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestOrder order = new TestOrder(123, user);
    String json = objectMapper.writeValueAsString(order);

    assertTrue(json.contains("id"));
    assertTrue(json.contains("user"));
    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testEmptyFields() throws JsonProcessingException {
    FieldsExpression fields = FieldsExpression.empty();
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json = objectMapper.writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertTrue(json.contains("password"));
  }

  @Test
  void testWildcardIncludesAll() throws JsonProcessingException {
    FieldsExpression fields = new FieldsExpression("*");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json = objectMapper.writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertTrue(json.contains("password"));
  }

  @Test
  void testMultipleExclusions() throws JsonProcessingException {
    FieldsExpression fields = new FieldsExpression("-email,-password");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    objectMapper.setFilterProvider(filterProvider);
    objectMapper.addMixIn(TestUser.class, TestFilterMixin.class);

    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json = objectMapper.writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertFalse(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @JsonFilter("testFilter")
  static class TestFilterMixin {

  }

  static class TestUser {

    private String name;
    private String email;
    private String password;

    public TestUser(String name, String email, String password) {
      this.name = name;
      this.email = email;
      this.password = password;
    }

    public String getName() {
      return name;
    }

    public String getEmail() {
      return email;
    }

    public String getPassword() {
      return password;
    }

  }

  static class TestOrder {

    private int id;
    private TestUser user;

    public TestOrder(int id, TestUser user) {
      this.id = id;
      this.user = user;
    }

    public int getId() {
      return id;
    }

    public TestUser getUser() {
      return user;
    }

  }

}
