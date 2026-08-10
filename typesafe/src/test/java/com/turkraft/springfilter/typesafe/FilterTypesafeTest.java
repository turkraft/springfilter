package com.turkraft.springfilter.typesafe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.definition.FilterInfixOperator;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.definition.FilterPostfixOperator;
import com.turkraft.springfilter.definition.FilterPrefixOperator;
import com.turkraft.springfilter.language.AndOperator;
import com.turkraft.springfilter.language.EqualOperator;
import com.turkraft.springfilter.language.GreaterThanOperator;
import com.turkraft.springfilter.language.GreaterThanOrEqualOperator;
import com.turkraft.springfilter.language.InOperator;
import com.turkraft.springfilter.language.IsEmptyOperator;
import com.turkraft.springfilter.language.IsNotEmptyOperator;
import com.turkraft.springfilter.language.IsNotNullOperator;
import com.turkraft.springfilter.language.IsNullOperator;
import com.turkraft.springfilter.language.LessThanOperator;
import com.turkraft.springfilter.language.LessThanOrEqualOperator;
import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.language.NotEqualOperator;
import com.turkraft.springfilter.language.NotInOperator;
import com.turkraft.springfilter.language.NotOperator;
import com.turkraft.springfilter.language.OrOperator;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterStringTransformer;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

public class FilterTypesafeTest {

  private static FilterBuilder fb;
  private static FilterStringTransformer transformer;

  @BeforeAll
  static void setup() {
    List<FilterInfixOperator> infix = List.of(
        new AndOperator(), new OrOperator(),
        new EqualOperator(), new NotEqualOperator(),
        new GreaterThanOperator(), new GreaterThanOrEqualOperator(),
        new LessThanOperator(), new LessThanOrEqualOperator(),
        new LikeOperator(), new InOperator(), new NotInOperator());
    List<FilterPrefixOperator> prefix = List.of(new NotOperator());
    List<FilterPostfixOperator> postfix = List.of(
        new IsNullOperator(), new IsNotNullOperator(),
        new IsEmptyOperator(), new IsNotEmptyOperator());

    FilterOperators ops = FilterOperators.of(prefix, infix, postfix);
    fb = new FilterBuilder(ops);
    transformer = new FilterStringTransformer(new DefaultConversionService());
  }

  private static void assertFilter(String expected, FilterNode node) {
    assertEquals(expected, transformer.transform(node));
  }

  @Test
  void testIntEqual() {
    assertFilter("year : '2020'",
        TestEntityFilter.where(fb).year().equal(2020).build());
  }

  @Test
  void testIntBetween() {
    assertFilter("year between '2020' and '2025'",
        TestEntityFilter.where(fb).year().between(2020, 2025).build());
  }

  @Test
  void testStringStartsWith() {
    assertFilter("model ~ 'A%'",
        TestEntityFilter.where(fb).model().startsWith("A").build());
  }

  @Test
  void testDoubleGreaterThan() {
    assertFilter("price > '100.0'",
        TestEntityFilter.where(fb).price().greaterThan(100.0).build());
  }

  @Test
  void testBooleanTrue() {
    assertFilter("active : 'true'",
        TestEntityFilter.where(fb).active().isTrue().build());
  }

  @Test
  void testChainedAnd() {
    assertFilter("year between '2020' and '2025' and model ~ 'A%'",
        TestEntityFilter.where(fb)
            .year().between(2020, 2025)
            .and()
            .model().startsWith("A")
            .build());
  }

  @Test
  void testOrPrecedence() {
    assertFilter("year : '2020' or year : '2025' and active : 'true'",
        TestEntityFilter.where(fb)
            .year().equal(2020)
            .or()
            .year().equal(2025)
            .and()
            .active().isTrue()
            .build());
  }

  @Test
  void testIsNull() {
    assertFilter("model is null",
        TestEntityFilter.where(fb).model().isNull().build());
  }

  @Test
  void testIsEmpty() {
    assertFilter("model is empty",
        TestEntityFilter.where(fb).model().isEmpty().build());
  }

  @Test
  void testBuildEmptyThrows() {
    Assertions.assertThrows(IllegalStateException.class,
        () -> TestEntityFilter.where(fb).build());
  }

}
