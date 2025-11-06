package com.turkraft.springfilter.pagesort;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleSortParserTest {

  private SimpleSortParser parser;

  @BeforeEach
  void setUp() {
    parser = new SimpleSortParser();
  }

  @Test
  void testParseSingleFieldDefaultDirection() {
    SortExpression result = parser.parse("year");

    assertEquals(1, result.size());
    assertEquals("year", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.ASC, result.getFields().get(0).getDirection());
  }

  @Test
  void testParseSingleFieldAscending() {
    SortExpression result = parser.parse("year");

    assertEquals(1, result.size());
    assertEquals("year", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.ASC, result.getFields().get(0).getDirection());
  }

  @Test
  void testParseSingleFieldDescending() {
    SortExpression result = parser.parse("-year");

    assertEquals(1, result.size());
    assertEquals("year", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.DESC, result.getFields().get(0).getDirection());
  }

  @Test
  void testParseNestedField() {
    SortExpression result = parser.parse("brand.name");

    assertEquals(1, result.size());
    assertEquals("brand.name", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.ASC, result.getFields().get(0).getDirection());
  }

  @Test
  void testParseDeeplyNestedFieldDescending() {
    SortExpression result = parser.parse("-owner.company.address.city");

    assertEquals(1, result.size());
    assertEquals("owner.company.address.city", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.DESC, result.getFields().get(0).getDirection());
  }

  @Test
  void testParseMultipleFields() {
    SortExpression result = parser.parse("-year,brand.name");

    assertEquals(2, result.size());

    assertEquals("year", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.DESC, result.getFields().get(0).getDirection());

    assertEquals("brand.name", result.getFields().get(1).getFieldPath());
    assertEquals(SortDirection.ASC, result.getFields().get(1).getDirection());
  }

  @Test
  void testParseThreeFields() {
    SortExpression result = parser.parse("price,-createdAt,id");

    assertEquals(3, result.size());

    assertEquals("price", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.ASC, result.getFields().get(0).getDirection());

    assertEquals("createdAt", result.getFields().get(1).getFieldPath());
    assertEquals(SortDirection.DESC, result.getFields().get(1).getDirection());

    assertEquals("id", result.getFields().get(2).getFieldPath());
    assertEquals(SortDirection.ASC, result.getFields().get(2).getDirection());
  }

  @Test
  void testParseWithWhitespace() {
    SortExpression result = parser.parse("  -year  ,  name  ");

    assertEquals(2, result.size());
    assertEquals("year", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.DESC, result.getFields().get(0).getDirection());
    assertEquals("name", result.getFields().get(1).getFieldPath());
    assertEquals(SortDirection.ASC, result.getFields().get(1).getDirection());
  }

  @Test
  void testParseEmptyString() {
    SortExpression result = parser.parse("");
    assertTrue(result.isEmpty());
  }

  @Test
  void testParseNull() {
    SortExpression result = parser.parse(null);
    assertTrue(result.isEmpty());
  }

  @Test
  void testParseWithUnderscore() {
    SortExpression result = parser.parse("-created_at");
    assertEquals("created_at", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.DESC, result.getFields().get(0).getDirection());
  }

  @Test
  void testParseFieldPathStartsWithDot() {
    assertThrows(SortParseException.class, () -> parser.parse(".invalid"));
  }

  @Test
  void testParseFieldPathEndsWithDot() {
    assertThrows(SortParseException.class, () -> parser.parse("invalid."));
  }

  @Test
  void testParseFieldPathConsecutiveDots() {
    assertThrows(SortParseException.class, () -> parser.parse("field..name"));
  }

  @Test
  void testParseEmptyFieldInList() {
    assertThrows(SortParseException.class, () -> parser.parse("year,,name"));
  }

  @Test
  void testParseTooManyFields() {
    assertThrows(SortParseException.class,
        () -> parser.parse("f1,f2,f3,f4", 3));
  }

  @Test
  void testConversionToSpringSort() {
    SortExpression expression = parser.parse("-year,name");
    org.springframework.data.domain.Sort springSort = expression.toSpringSort();

    assertNotNull(springSort);
    assertTrue(springSort.isSorted());

    var orders = springSort.toList();
    assertEquals(2, orders.size());

    assertEquals("year", orders.get(0).getProperty());
    assertEquals(org.springframework.data.domain.Sort.Direction.DESC, orders.get(0).getDirection());

    assertEquals("name", orders.get(1).getProperty());
    assertEquals(org.springframework.data.domain.Sort.Direction.ASC, orders.get(1).getDirection());
  }

  @Test
  void testEmptyExpressionConvertsToUnsorted() {
    SortExpression expression = parser.parse("");
    org.springframework.data.domain.Sort springSort = expression.toSpringSort();

    assertNotNull(springSort);
    assertFalse(springSort.isSorted());
  }

  @Test
  void testParseVeryLongFieldPath() {
    String longPath = "a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p";
    SortExpression result = parser.parse(longPath);
    assertEquals(longPath, result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.ASC, result.getFields().get(0).getDirection());
  }

  @Test
  void testMaxFieldsDefault() {
    String expression = "f1,f2,f3,f4,f5";
    SortExpression result = parser.parse(expression);
    assertEquals(5, result.size());
  }

  @Test
  void testMaxFieldsEnforced() {
    String expression = "f1,f2,f3";
    assertThrows(SortParseException.class, () -> parser.parse(expression, 2));
  }

  @Test
  void testMaxFieldsExactly() {
    String expression = "f1,f2";
    SortExpression result = parser.parse(expression, 2);
    assertEquals(2, result.size());
  }

  @Test
  void testParseFieldStartingWithNumber() {
    assertThrows(SortParseException.class, () -> parser.parse("123invalid"));
  }

  @Test
  void testParseOnlyWhitespace() {
    SortExpression result = parser.parse("   ");
    assertTrue(result.isEmpty());
  }

  @Test
  void testParseFieldWithSpecialCharacters() {
    assertThrows(SortParseException.class, () -> parser.parse("field@name"));
    assertThrows(SortParseException.class, () -> parser.parse("field$name"));
  }

  @Test
  void testParseDescendingNestedField() {
    SortExpression result = parser.parse("-user.name");
    assertEquals("user.name", result.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.DESC, result.getFields().get(0).getDirection());
  }

  @Test
  void testParseAllDescending() {
    SortExpression result = parser.parse("-year,-name,-id");
    assertEquals(3, result.size());
    assertEquals(SortDirection.DESC, result.getFields().get(0).getDirection());
    assertEquals(SortDirection.DESC, result.getFields().get(1).getDirection());
    assertEquals(SortDirection.DESC, result.getFields().get(2).getDirection());
  }

  @Test
  void testParseAllAscending() {
    SortExpression result = parser.parse("year,name,id");
    assertEquals(3, result.size());
    assertEquals(SortDirection.ASC, result.getFields().get(0).getDirection());
    assertEquals(SortDirection.ASC, result.getFields().get(1).getDirection());
    assertEquals(SortDirection.ASC, result.getFields().get(2).getDirection());
  }

  @Test
  void testParseEmptyFieldAfterMinus() {
    assertThrows(SortParseException.class, () -> parser.parse("-"));
  }

  @Test
  void testParseMultipleMinuses() {
    assertThrows(SortParseException.class, () -> parser.parse("--year"));
  }

}
