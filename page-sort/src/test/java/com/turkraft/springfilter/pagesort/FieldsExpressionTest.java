package com.turkraft.springfilter.pagesort;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.Test;

class FieldsExpressionTest {

  @Test
  void testConstructorWithSingleField() {
    FieldsExpression expression = new FieldsExpression("name");

    assertEquals(1, expression.size());
    assertArrayEquals(new String[]{"name"}, expression.getPatterns());
    assertTrue(expression.hasIncludes());
    assertFalse(expression.hasExcludes());
  }

  @Test
  void testConstructorWithMultipleFields() {
    FieldsExpression expression = new FieldsExpression("id,name,email");

    assertEquals(3, expression.size());
    assertArrayEquals(new String[]{"id", "name", "email"}, expression.getPatterns());
    assertTrue(expression.hasIncludes());
    assertFalse(expression.hasExcludes());
  }

  @Test
  void testConstructorWithWildcard() {
    FieldsExpression expression = new FieldsExpression("*");

    assertEquals(1, expression.size());
    assertArrayEquals(new String[]{"*"}, expression.getPatterns());
    assertTrue(expression.hasIncludes());
    assertFalse(expression.hasExcludes());
  }

  @Test
  void testConstructorWithNestedFields() {
    FieldsExpression expression = new FieldsExpression("user.name,user.email");

    assertEquals(2, expression.size());
    assertArrayEquals(new String[]{"user.name", "user.email"}, expression.getPatterns());
  }

  @Test
  void testConstructorWithWildcardPattern() {
    FieldsExpression expression = new FieldsExpression("user.*");

    assertEquals(1, expression.size());
    assertArrayEquals(new String[]{"user.*"}, expression.getPatterns());
  }

  @Test
  void testConstructorWithExclusion() {
    FieldsExpression expression = new FieldsExpression("-password");

    assertEquals(1, expression.size());
    assertFalse(expression.hasIncludes());
    assertTrue(expression.hasExcludes());
    assertTrue(expression.getExcludePatterns().contains("password"));
  }

  @Test
  void testConstructorWithExclusionUsingExclamation() {
    FieldsExpression expression = new FieldsExpression("!password");

    assertEquals(1, expression.size());
    assertFalse(expression.hasIncludes());
    assertTrue(expression.hasExcludes());
    assertTrue(expression.getExcludePatterns().contains("password"));
  }

  @Test
  void testConstructorWithIncludesAndExcludes() {
    FieldsExpression expression = new FieldsExpression("*,-password,-secret");

    assertEquals(3, expression.size());
    assertTrue(expression.hasIncludes());
    assertTrue(expression.hasExcludes());
    assertEquals(Set.of("*"), expression.getIncludePatterns());
    assertEquals(Set.of("password", "secret"), expression.getExcludePatterns());
  }

  @Test
  void testConstructorWithWhitespace() {
    FieldsExpression expression = new FieldsExpression(" id , name , email ");

    assertEquals(3, expression.size());
    assertArrayEquals(new String[]{"id", "name", "email"}, expression.getPatterns());
  }

  @Test
  void testConstructorWithNull() {
    FieldsExpression expression = new FieldsExpression((String) null);

    assertTrue(expression.isEmpty());
    assertEquals(0, expression.size());
  }

  @Test
  void testConstructorWithEmptyString() {
    FieldsExpression expression = new FieldsExpression("");

    assertTrue(expression.isEmpty());
    assertEquals(0, expression.size());
  }

  @Test
  void testConstructorWithOnlyWhitespace() {
    FieldsExpression expression = new FieldsExpression("   ");

    assertTrue(expression.isEmpty());
    assertEquals(0, expression.size());
  }

  @Test
  void testConstructorWithVarargs() {
    FieldsExpression expression = new FieldsExpression("id", "name", "email");

    assertEquals(3, expression.size());
    assertArrayEquals(new String[]{"id", "name", "email"}, expression.getPatterns());
  }

  @Test
  void testConstructorVarargsFiltersNullAndEmpty() {
    FieldsExpression expression = new FieldsExpression("id", null, "", "name", "  ");

    assertEquals(2, expression.size());
    assertArrayEquals(new String[]{"id", "name"}, expression.getPatterns());
  }

  @Test
  void testConstructorVarargsThrowsOnNull() {
    assertThrows(NullPointerException.class, () -> new FieldsExpression((String[]) null));
  }

  @Test
  void testEmpty() {
    FieldsExpression expression = FieldsExpression.empty();

    assertTrue(expression.isEmpty());
    assertEquals(0, expression.size());
    assertFalse(expression.hasIncludes());
    assertFalse(expression.hasExcludes());
  }

  @Test
  void testGetPatternsReturnsDefensiveCopy() {
    FieldsExpression expression = new FieldsExpression("id,name");
    String[] patterns = expression.getPatterns();
    patterns[0] = "modified";

    assertArrayEquals(new String[]{"id", "name"}, expression.getPatterns());
  }

  @Test
  void testGetIncludePatternsIsImmutable() {
    FieldsExpression expression = new FieldsExpression("id,name");
    Set<String> includes = expression.getIncludePatterns();

    assertThrows(UnsupportedOperationException.class, () -> includes.add("email"));
  }

  @Test
  void testGetExcludePatternsIsImmutable() {
    FieldsExpression expression = new FieldsExpression("*,-password");
    Set<String> excludes = expression.getExcludePatterns();

    assertThrows(UnsupportedOperationException.class, () -> excludes.add("secret"));
  }

  @Test
  void testInvalidPatternStartingWithNumber() {
    assertThrows(FieldsParseException.class, () -> new FieldsExpression("123invalid"));
  }

  @Test
  void testInvalidPatternWithSpecialCharacters() {
    assertThrows(FieldsParseException.class, () -> new FieldsExpression("field@name"));
    assertThrows(FieldsParseException.class, () -> new FieldsExpression("field#name"));
    assertThrows(FieldsParseException.class, () -> new FieldsExpression("field$name"));
  }

  @Test
  void testInvalidPatternWithSpace() {
    assertThrows(FieldsParseException.class, () -> new FieldsExpression("field name"));
  }

  @Test
  void testValidPatternWithUnderscore() {
    FieldsExpression expression = new FieldsExpression("field_name");
    assertEquals(1, expression.size());
    assertArrayEquals(new String[]{"field_name"}, expression.getPatterns());
  }

  @Test
  void testValidPatternStartingWithUnderscore() {
    FieldsExpression expression = new FieldsExpression("_id");
    assertEquals(1, expression.size());
    assertArrayEquals(new String[]{"_id"}, expression.getPatterns());
  }

  @Test
  void testNestedFieldWithWildcard() {
    FieldsExpression expression = new FieldsExpression("user.*.name");
    assertEquals(1, expression.size());
    assertArrayEquals(new String[]{"user.*.name"}, expression.getPatterns());
  }

  @Test
  void testComplexPattern() {
    FieldsExpression expression = new FieldsExpression(
        "id,name,user.*,order.items,-user.password,-order.secret");

    assertEquals(6, expression.size());
    assertTrue(expression.hasIncludes());
    assertTrue(expression.hasExcludes());
    assertEquals(Set.of("id", "name", "user.*", "order.items"),
        expression.getIncludePatterns());
    assertEquals(Set.of("user.password", "order.secret"), expression.getExcludePatterns());
  }

  @Test
  void testEquals() {
    FieldsExpression expression1 = new FieldsExpression("id,name");
    FieldsExpression expression2 = new FieldsExpression("id,name");
    FieldsExpression expression3 = new FieldsExpression("id,email");

    assertEquals(expression1, expression2);
    assertNotEquals(expression1, expression3);
    assertNotEquals(expression1, null);
  }

  @Test
  void testHashCode() {
    FieldsExpression expression1 = new FieldsExpression("id,name");
    FieldsExpression expression2 = new FieldsExpression("id,name");

    assertEquals(expression1.hashCode(), expression2.hashCode());
  }

  @Test
  void testToString() {
    FieldsExpression expression1 = new FieldsExpression("id,name,email");
    assertEquals("id, name, email", expression1.toString());

    FieldsExpression expression2 = FieldsExpression.empty();
    assertEquals("empty", expression2.toString());
  }

  @Test
  void testToStringWithExclusions() {
    FieldsExpression expression = new FieldsExpression("*,-password");
    assertEquals("*, -password", expression.toString());
  }

  @Test
  void testMultipleWildcards() {
    FieldsExpression expression = new FieldsExpression("user.*,order.*,product.*");
    assertEquals(3, expression.size());
    assertTrue(expression.hasIncludes());
    assertFalse(expression.hasExcludes());
  }

  @Test
  void testExclusionWithWildcard() {
    FieldsExpression expression = new FieldsExpression("*,-user.*");
    assertEquals(2, expression.size());
    assertTrue(expression.hasIncludes());
    assertTrue(expression.hasExcludes());
    assertEquals(Set.of("*"), expression.getIncludePatterns());
    assertEquals(Set.of("user.*"), expression.getExcludePatterns());
  }

  @Test
  void testMixedExclusionPrefixes() {
    FieldsExpression expression = new FieldsExpression("*,-password,!secret");
    assertEquals(3, expression.size());
    assertEquals(Set.of("password", "secret"), expression.getExcludePatterns());
  }

  @Test
  void testDeepNestedFields() {
    FieldsExpression expression = new FieldsExpression("a.b.c.d.e.f");
    assertEquals(1, expression.size());
    assertArrayEquals(new String[]{"a.b.c.d.e.f"}, expression.getPatterns());
  }

}
