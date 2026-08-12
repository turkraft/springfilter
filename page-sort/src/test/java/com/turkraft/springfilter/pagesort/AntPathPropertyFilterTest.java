package com.turkraft.springfilter.pagesort;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.annotation.JsonFilter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AntPathPropertyFilterTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = JsonMapper.builder()
        .addMixIn(TestUser.class, TestFilterMixin.class)
        .addMixIn(TestOrder.class, TestFilterMixin.class)
        .addMixIn(TestPage.class, TestFilterMixin.class)
        .build();
  }

  @Test
  void testConstructorWithNullFieldsThrowsException() {
    assertThrows(NullPointerException.class, () -> new AntPathPropertyFilter(null));
  }

  @Test
  void testIncludeSingleField()
      throws Exception {
    FieldsExpression fields = new FieldsExpression("name");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json =     objectMapper.writer(filterProvider).writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("John"));
    assertFalse(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testIncludeMultipleFields()
      throws Exception {
    FieldsExpression fields = new FieldsExpression("name,email");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json =     objectMapper.writer(filterProvider).writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testExcludeField()
      throws Exception {
    FieldsExpression fields = new FieldsExpression("-password");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json =     objectMapper.writer(filterProvider).writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testIncludeAllExcludeOne()
      throws Exception {
    FieldsExpression fields = new FieldsExpression("*,-password");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json =     objectMapper.writer(filterProvider).writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testNestedFieldInclusion()
      throws Exception {
    FieldsExpression fields = new FieldsExpression("user.name");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    
    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestOrder order = new TestOrder(123, user);
    String json =     objectMapper.writer(filterProvider).writeValueAsString(order);

    assertTrue(json.contains("user"));
    assertTrue(json.contains("name"));
    assertTrue(json.contains("John"));
    assertFalse(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testNestedFieldWildcard()
      throws Exception {
    FieldsExpression fields = new FieldsExpression("user.*");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    
    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestOrder order = new TestOrder(123, user);
    String json =     objectMapper.writer(filterProvider).writeValueAsString(order);

    assertTrue(json.contains("user"));
    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertTrue(json.contains("password"));
    assertFalse(json.contains("\"id\""));
  }

  @Test
  void testExcludeNestedField()
      throws Exception {
    FieldsExpression fields = new FieldsExpression("*,-user.password");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);

    
    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestOrder order = new TestOrder(123, user);
    String json =     objectMapper.writer(filterProvider).writeValueAsString(order);

    assertTrue(json.contains("id"));
    assertTrue(json.contains("user"));
    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testEmptyFields()
      throws Exception {
    FieldsExpression fields = FieldsExpression.empty();
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json =     objectMapper.writer(filterProvider).writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertTrue(json.contains("password"));
  }

  @Test
  void testWildcardIncludesAll()
      throws Exception {
    FieldsExpression fields = new FieldsExpression("*");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json =     objectMapper.writer(filterProvider).writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertTrue(json.contains("email"));
    assertTrue(json.contains("password"));
  }

  @Test
  void testMultipleExclusions()
      throws Exception {
    FieldsExpression fields = new FieldsExpression("-email,-password");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    String json =     objectMapper.writer(filterProvider).writeValueAsString(user);

    assertTrue(json.contains("name"));
    assertFalse(json.contains("email"));
    assertFalse(json.contains("password"));
  }

  @Test
  void testRootOnlyFiltersWithinRoot() throws Exception {
    FieldsExpression fields = new FieldsExpression("content", "name,email");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestPage<TestUser> page = new TestPage<>(user, 100, 10);
    String json = objectMapper.writer(filterProvider).writeValueAsString(page);

    assertTrue(json.contains("\"totalElements\""));
    assertTrue(json.contains("\"totalPages\""));
    assertTrue(json.contains("\"content\""));
    assertTrue(json.contains("\"name\""));
    assertTrue(json.contains("\"email\""));
    assertFalse(json.contains("\"password\""));
  }

  @Test
  void testRootPreservesMetadataFields() throws Exception {
    FieldsExpression fields = new FieldsExpression("content", "id,name");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestPage<TestUser> page = new TestPage<>(user, 1, 5);
    String json = objectMapper.writer(filterProvider).writeValueAsString(page);

    assertTrue(json.contains("\"totalElements\""));
    assertTrue(json.contains("\"totalPages\""));
    assertFalse(json.contains("\"email\""));
    assertFalse(json.contains("\"password\""));
  }

  @Test
  void testRootWithWildcard() throws Exception {
    FieldsExpression fields = new FieldsExpression("content", "name,*");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestPage<TestUser> page = new TestPage<>(user, 10, 2);
    String json = objectMapper.writer(filterProvider).writeValueAsString(page);

    assertTrue(json.contains("\"totalElements\""));
    assertTrue(json.contains("\"totalPages\""));
    assertTrue(json.contains("\"name\""));
    assertTrue(json.contains("\"email\""));
    assertTrue(json.contains("\"password\""));
    assertFalse(json.contains("\"id\""));
  }

  @Test
  void testRootWithExclusion() throws Exception {
    FieldsExpression fields = new FieldsExpression("content", "*,-password");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestPage<TestUser> page = new TestPage<>(user, 50, 5);
    String json = objectMapper.writer(filterProvider).writeValueAsString(page);

    assertTrue(json.contains("\"totalElements\""));
    assertTrue(json.contains("\"totalPages\""));
    assertTrue(json.contains("\"name\""));
    assertTrue(json.contains("\"email\""));
    assertFalse(json.contains("\"password\""));
  }

  @Test
  void testRootStripsTrailingDots() throws Exception {
    FieldsExpression fields = new FieldsExpression("content.", "name,email");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestPage<TestUser> page = new TestPage<>(user, 100, 10);
    String json = objectMapper.writer(filterProvider).writeValueAsString(page);

    assertTrue(json.contains("\"totalElements\""));
    assertTrue(json.contains("\"totalPages\""));
    assertTrue(json.contains("\"name\""));
    assertTrue(json.contains("\"email\""));
    assertFalse(json.contains("\"password\""));
  }

  @Test
  void testNoRootFiltersEntireObject() throws Exception {
    FieldsExpression fields = new FieldsExpression("content.name,content.email");
    AntPathPropertyFilter filter = new AntPathPropertyFilter(fields);

    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("testFilter", filter);


    TestUser user = new TestUser("John", "john@example.com", "secret123");
    TestPage<TestUser> page = new TestPage<>(user, 100, 10);
    String json = objectMapper.writer(filterProvider).writeValueAsString(page);

    assertFalse(json.contains("\"totalElements\""));
    assertFalse(json.contains("\"totalPages\""));
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

  static class TestPage<T> {

    private T content;
    private long totalElements;
    private int totalPages;

    public TestPage(T content, long totalElements, int totalPages) {
      this.content = content;
      this.totalElements = totalElements;
      this.totalPages = totalPages;
    }

    public T getContent() {
      return content;
    }

    public long getTotalElements() {
      return totalElements;
    }

    public int getTotalPages() {
      return totalPages;
    }

  }

}
