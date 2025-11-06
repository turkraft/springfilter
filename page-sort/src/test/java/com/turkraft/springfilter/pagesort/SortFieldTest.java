package com.turkraft.springfilter.pagesort;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class SortFieldTest {

  @Test
  void testConstructorWithDirection() {
    SortField field = new SortField("name", SortDirection.DESC);
    assertEquals("name", field.getFieldPath());
    assertEquals(SortDirection.DESC, field.getDirection());
  }

  @Test
  void testConstructorWithoutDirection() {
    SortField field = new SortField("name");
    assertEquals("name", field.getFieldPath());
    assertEquals(SortDirection.ASC, field.getDirection());
  }

  @Test
  void testConstructorNullDirectionDefaultsToAsc() {
    SortField field = new SortField("name", null);
    assertEquals(SortDirection.ASC, field.getDirection());
  }

  @Test
  void testConstructorNullFieldPathThrows() {
    assertThrows(IllegalArgumentException.class, () -> new SortField(null, SortDirection.ASC));
  }

  @Test
  void testConstructorEmptyFieldPathThrows() {
    assertThrows(IllegalArgumentException.class, () -> new SortField("", SortDirection.ASC));
    assertThrows(IllegalArgumentException.class, () -> new SortField("   ", SortDirection.ASC));
  }

  @Test
  void testToSpringOrder() {
    SortField field = new SortField("name", SortDirection.DESC);
    Sort.Order order = field.toSpringOrder();

    assertEquals("name", order.getProperty());
    assertEquals(Sort.Direction.DESC, order.getDirection());
  }

  @Test
  void testToString() {
    SortField field1 = new SortField("name", SortDirection.ASC);
    assertEquals("name", field1.toString());

    SortField field2 = new SortField("price", SortDirection.DESC);
    assertEquals("-price", field2.toString());
  }

  @Test
  void testEquals() {
    SortField field1 = new SortField("name", SortDirection.ASC);
    SortField field2 = new SortField("name", SortDirection.ASC);
    SortField field3 = new SortField("name", SortDirection.DESC);
    SortField field4 = new SortField("price", SortDirection.ASC);

    assertEquals(field1, field2);
    assertNotEquals(field1, field3);
    assertNotEquals(field1, field4);
    assertNotEquals(field1, null);
    assertNotEquals(field1, "string");
  }

  @Test
  void testHashCode() {
    SortField field1 = new SortField("name", SortDirection.ASC);
    SortField field2 = new SortField("name", SortDirection.ASC);

    assertEquals(field1.hashCode(), field2.hashCode());
  }

  @Test
  void testTrimFieldPath() {
    SortField field = new SortField("  name  ", SortDirection.ASC);
    assertEquals("name", field.getFieldPath());
  }

}
