package com.turkraft.springfilter.pagesort;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class SortExpressionTest {

  @Test
  void testConstructorWithList() {
    List<SortField> fields = List.of(
        new SortField("name", SortDirection.ASC),
        new SortField("price", SortDirection.DESC)
    );

    SortExpression expression = new SortExpression(fields);

    assertEquals(2, expression.size());
    assertEquals(fields, expression.getFields());
  }

  @Test
  void testConstructorWithVarargs() {
    SortExpression expression = new SortExpression(
        new SortField("name", SortDirection.ASC),
        new SortField("price", SortDirection.DESC)
    );

    assertEquals(2, expression.size());
  }

  @Test
  void testConstructorWithNullList() {
    SortExpression expression = new SortExpression((List<SortField>) null);
    assertTrue(expression.isEmpty());
    assertEquals(0, expression.size());
  }

  @Test
  void testConstructorWithEmptyList() {
    SortExpression expression = new SortExpression(Collections.emptyList());
    assertTrue(expression.isEmpty());
    assertEquals(0, expression.size());
  }

  @Test
  void testUnsorted() {
    SortExpression expression = SortExpression.unsorted();
    assertTrue(expression.isEmpty());
    assertEquals(0, expression.size());
  }

  @Test
  void testToSpringSort() {
    SortExpression expression = new SortExpression(
        new SortField("name", SortDirection.ASC),
        new SortField("price", SortDirection.DESC)
    );

    Sort springSort = expression.toSpringSort();

    assertTrue(springSort.isSorted());
    List<Sort.Order> orders = springSort.toList();
    assertEquals(2, orders.size());

    assertEquals("name", orders.get(0).getProperty());
    assertEquals(Sort.Direction.ASC, orders.get(0).getDirection());

    assertEquals("price", orders.get(1).getProperty());
    assertEquals(Sort.Direction.DESC, orders.get(1).getDirection());
  }

  @Test
  void testToSpringSortEmpty() {
    SortExpression expression = SortExpression.unsorted();
    Sort springSort = expression.toSpringSort();

    assertFalse(springSort.isSorted());
  }

  @Test
  void testToString() {
    SortExpression expression1 = new SortExpression(
        new SortField("name", SortDirection.ASC),
        new SortField("price", SortDirection.DESC)
    );
    assertEquals("name,-price", expression1.toString());

    SortExpression expression2 = SortExpression.unsorted();
    assertEquals("unsorted", expression2.toString());
  }

  @Test
  void testEquals() {
    SortExpression expression1 = new SortExpression(new SortField("name", SortDirection.ASC));
    SortExpression expression2 = new SortExpression(new SortField("name", SortDirection.ASC));
    SortExpression expression3 = new SortExpression(new SortField("price", SortDirection.ASC));

    assertEquals(expression1, expression2);
    assertNotEquals(expression1, expression3);
    assertNotEquals(expression1, null);
  }

  @Test
  void testHashCode() {
    SortExpression expression1 = new SortExpression(new SortField("name", SortDirection.ASC));
    SortExpression expression2 = new SortExpression(new SortField("name", SortDirection.ASC));

    assertEquals(expression1.hashCode(), expression2.hashCode());
  }

  @Test
  void testFieldsAreImmutable() {
    List<SortField> fields = List.of(new SortField("name", SortDirection.ASC));
    SortExpression expression = new SortExpression(fields);

    assertThrows(UnsupportedOperationException.class, () -> expression.getFields().clear());
  }

}
