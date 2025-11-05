package com.turkraft.springfilter;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.language.SizeFunction;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FilterPredicateTransformerTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private ConversionService conversionService;

  @Autowired
  private FilterBuilder fb;

  @Autowired
  private FilterNodeProcessorFactories filterNodeProcessorFactories;

  @Autowired
  private SizeFunction sizeFunction;

  private FilterPredicateTransformer transformer;

  @BeforeEach
  void init() {
    transformer = new FilterPredicateTransformer(conversionService,
        filterNodeProcessorFactories, TestPojo.class);
  }

  @Test
  void testEqualWithInput() {

    FilterNode filter = fb.input("hello").equal(fb.input("hello")).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));

  }

  @Test
  void testEqualWithField() {

    FilterNode filter = fb.field("string").equal(fb.input("hello")).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("world", List.of(), 100)));

  }

  @Test
  void testNotEqual() {

    FilterNode filter = fb.field("string").notEqual(fb.input("hello")).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertFalse(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("world", List.of(), 100)));

  }

  @Test
  void testGreaterThan() {

    FilterNode filter = fb.field("integer").greaterThan(fb.input(50)).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(), 25)));

  }

  @Test
  void testGreaterThanOrEqual() {

    FilterNode filter = fb.field("integer").greaterThanOrEqual(fb.input(100)).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(), 150)));
    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(), 50)));

  }

  @Test
  void testLessThan() {

    FilterNode filter = fb.field("integer").lessThan(fb.input(100)).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(), 50)));
    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(), 150)));

  }

  @Test
  void testLessThanOrEqual() {

    FilterNode filter = fb.field("integer").lessThanOrEqual(fb.input(100)).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(), 50)));
    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(), 150)));

  }

  @Test
  void testAnd() {

    FilterNode filter = fb.field("string").equal(fb.input("hello"))
        .and(fb.field("integer").greaterThan(fb.input(50))).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("world", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("hello", List.of(), 25)));

  }

  @Test
  void testOr() {

    FilterNode filter = fb.field("string").equal(fb.input("hello"))
        .or(fb.field("integer").greaterThan(fb.input(50))).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 25)));
    Assertions.assertTrue(predicate.test(new TestPojo("world", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("world", List.of(), 25)));

  }

  @Test
  void testNot() {

    FilterNode filter = fb.field("string").equal(fb.input("hello")).not().get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertFalse(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("world", List.of(), 100)));

  }

  @Test
  void testIsNull() {

    FilterNode filter = fb.field("string").isNull().get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo(null, List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("hello", List.of(), 100)));

  }

  @Test
  void testIsNotNull() {

    FilterNode filter = fb.field("string").isNotNull().get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertFalse(predicate.test(new TestPojo(null, List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));

  }

  @Test
  void testIsEmpty() {

    FilterNode filter = fb.field("integers").isEmpty().get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(1, 2, 3), 100)));

  }

  @Test
  void testIsNotEmpty() {

    FilterNode filter = fb.field("integers").isNotEmpty().get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(1, 2, 3), 100)));

  }

  @Test
  void testIn() {

    FilterNode filter = fb.field("string")
        .in(fb.collection(fb.input("hello"), fb.input("world"))).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("world", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(), 100)));

  }

  @Test
  void testNotIn() {

    FilterNode filter = fb.field("string")
        .notIn(fb.collection(fb.input("hello"), fb.input("world"))).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertFalse(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("world", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(), 100)));

  }

  @Test
  void testLike() {

    FilterNode filter = fb.field("string").like(fb.input("hel%")).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("help", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("world", List.of(), 100)));

  }

  @Test
  void testComplexQuery() {

    FilterNode filter = fb.field("string").equal(fb.input("hello"))
        .and(fb.field("integer").greaterThan(fb.input(50)))
        .or(fb.field("integers").isNotEmpty()).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("world", List.of(1, 2, 3), 25)));
    Assertions.assertFalse(predicate.test(new TestPojo("world", List.of(), 25)));

  }

  @Test
  void testSizeFunction() {

    FilterNode filter = fb.function(sizeFunction, fb.field("integers"))
        .greaterThan(fb.input(0)).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(1, 2, 3), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(), 100)));

  }

  @Test
  void testNestedFields() {

    FilterNode filter = fb.field("string").equal(fb.input("hello"))
        .and(
            fb.field("integer").greaterThan(fb.input(50))
                .or(fb.field("integers").isNotEmpty())
        ).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(1), 25)));
    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("world", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("hello", List.of(), 25)));

  }

  @Test
  void testSizeFunctionWithString() {

    FilterNode filter = fb.function(sizeFunction, fb.field("string"))
        .equal(fb.input(5)).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("world!", List.of(), 100)));

  }

  @Test
  void testInsensitiveLike() {

    FilterNode filter = fb.field("string").insensitiveLike(fb.input("HEL%")).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("HELLO", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("Help", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("world", List.of(), 100)));

  }

  @Test
  void testNestedFieldAccess() {

    FilterNode filter = fb.field("nested.name").equal(fb.input("test")).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(
        new TestPojo("hello", List.of(), 100, null, new TestPojo.NestedPojo("test", 42))));
    Assertions.assertFalse(predicate.test(
        new TestPojo("hello", List.of(), 100, null, new TestPojo.NestedPojo("other", 42))));

  }

  @Test
  void testNestedFieldWithNull() {

    FilterNode filter = fb.field("nested.name").isNull().get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100, null, null)));
    Assertions.assertFalse(predicate.test(
        new TestPojo("hello", List.of(), 100, null, new TestPojo.NestedPojo("test", 42))));

  }

  @Test
  void testIsEmptyWithString() {

    FilterNode filter = fb.field("string").isEmpty().get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("hello", List.of(), 100)));

  }

  @Test
  void testIsEmptyWithMap() {

    FilterNode filter = fb.field("metadata").isEmpty().get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(), 100, Map.of(), null)));
    Assertions.assertFalse(
        predicate.test(new TestPojo("test", List.of(), 100, Map.of("key", "value"), null)));

  }

  @Test
  void testIsNotEmptyWithMap() {

    FilterNode filter = fb.field("metadata").isNotEmpty().get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(), 100, Map.of(), null)));
    Assertions.assertTrue(
        predicate.test(new TestPojo("test", List.of(), 100, Map.of("key", "value"), null)));

  }

  @Test
  void testSizeFunctionWithMap() {

    FilterNode filter = fb.function(sizeFunction, fb.field("metadata"))
        .equal(fb.input(2)).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(
        new TestPojo("test", List.of(), 100, Map.of("key1", "value1", "key2", "value2"), null)));
    Assertions.assertFalse(
        predicate.test(new TestPojo("test", List.of(), 100, Map.of("key", "value"), null)));

  }

  @Test
  void testSizeFunctionWithNull() {

    FilterNode filter = fb.function(sizeFunction, fb.field("string"))
        .equal(fb.input(0)).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo(null, List.of(), 100)));

  }

  @Test
  void testInWithFieldReference() {

    FilterNode filter = fb.field("integer")
        .in(fb.field("integers")).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("test", List.of(50, 100, 150), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("test", List.of(50, 150), 100)));

  }

  @Test
  void testLikeWithUnderscore() {

    FilterNode filter = fb.field("string").like(fb.input("hel_o")).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(new TestPojo("hello", List.of(), 100)));
    Assertions.assertTrue(predicate.test(new TestPojo("helpo", List.of(), 100)));
    Assertions.assertFalse(predicate.test(new TestPojo("helo", List.of(), 100)));

  }

  @Test
  void testComplexNestedQuery() {

    FilterNode filter = fb.field("nested.value").greaterThan(fb.input(50))
        .and(fb.field("integer").lessThan(fb.input(200)))
        .or(fb.field("string").like(fb.input("%test%"))).get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertTrue(predicate.test(
        new TestPojo("hello", List.of(), 100, null, new TestPojo.NestedPojo("name", 100))));
    Assertions.assertTrue(predicate.test(new TestPojo("testing", List.of(), 500, null, null)));
    Assertions.assertFalse(predicate.test(
        new TestPojo("hello", List.of(), 500, null, new TestPojo.NestedPojo("name", 10))));

  }

}
