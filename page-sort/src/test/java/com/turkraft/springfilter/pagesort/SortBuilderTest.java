package com.turkraft.springfilter.pagesort;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class SortBuilderTest {

  @Test
  void testBuildSingleFieldDefaultDirection() {
    SortBuilder builder = new SortBuilder();
    SortExpression expression = builder.field("name").build();

    assertEquals(1, expression.size());
    assertEquals("name", expression.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.ASC, expression.getFields().get(0).getDirection());
  }

  @Test
  void testBuildSingleFieldAsc() {
    SortBuilder builder = new SortBuilder();
    SortExpression expression = builder.field("name").asc().build();

    assertEquals(1, expression.size());
    assertEquals(SortDirection.ASC, expression.getFields().get(0).getDirection());
  }

  @Test
  void testBuildSingleFieldDesc() {
    SortBuilder builder = new SortBuilder();
    SortExpression expression = builder.field("name").desc().build();

    assertEquals(1, expression.size());
    assertEquals(SortDirection.DESC, expression.getFields().get(0).getDirection());
  }

  @Test
  void testBuildMultipleFields() {
    SortBuilder builder = new SortBuilder();
    SortExpression expression = builder
        .field("year").desc()
        .field("name").asc()
        .build();

    assertEquals(2, expression.size());

    assertEquals("year", expression.getFields().get(0).getFieldPath());
    assertEquals(SortDirection.DESC, expression.getFields().get(0).getDirection());

    assertEquals("name", expression.getFields().get(1).getFieldPath());
    assertEquals(SortDirection.ASC, expression.getFields().get(1).getDirection());
  }

  @Test
  void testBuildThreeFields() {
    SortBuilder builder = new SortBuilder();
    SortExpression expression = builder
        .field("price").asc()
        .field("createdAt").desc()
        .field("id").asc()
        .build();

    assertEquals(3, expression.size());
  }

  @Test
  void testBuildEmpty() {
    SortBuilder builder = new SortBuilder();
    SortExpression expression = builder.build();

    assertTrue(expression.isEmpty());
  }

  @Test
  void testBuildSpringSort() {
    SortBuilder builder = new SortBuilder();
    Sort springSort = builder
        .field("year").desc()
        .field("name").asc()
        .buildSpringSort();

    assertTrue(springSort.isSorted());
    assertEquals(2, springSort.toList().size());
  }

  @Test
  void testDirectionMethod() {
    SortBuilder builder = new SortBuilder();
    SortExpression expression = builder
        .field("name").direction(SortDirection.DESC)
        .build();

    assertEquals(SortDirection.DESC, expression.getFields().get(0).getDirection());
  }

  @Test
  void testReset() {
    SortBuilder builder = new SortBuilder();
    builder.field("name").desc();
    builder.reset();

    SortExpression expression = builder.build();
    assertTrue(expression.isEmpty());
  }

  @Test
  void testReusableBuilder() {
    SortBuilder builder = new SortBuilder();

    SortExpression expression1 = builder.field("name").desc().build();
    assertEquals(1, expression1.size());

    builder.reset();

    SortExpression expression2 = builder.field("price").asc().build();
    assertEquals(1, expression2.size());
    assertEquals("price", expression2.getFields().get(0).getFieldPath());
  }

  @Test
  void testNestedFieldPath() {
    SortBuilder builder = new SortBuilder();
    SortExpression expression = builder
        .field("brand.name").asc()
        .field("owner.company.name").desc()
        .build();

    assertEquals(2, expression.size());
    assertEquals("brand.name", expression.getFields().get(0).getFieldPath());
    assertEquals("owner.company.name", expression.getFields().get(1).getFieldPath());
  }

}
