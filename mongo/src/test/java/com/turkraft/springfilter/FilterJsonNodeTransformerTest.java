package com.turkraft.springfilter;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.language.SizeFunction;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
public class FilterJsonNodeTransformerTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private ConversionService conversionService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private FilterBuilder fb;

  @Autowired
  private FilterNodeProcessorFactories filterNodeProcessorFactories;

  @Autowired
  private FieldTypeResolver fieldTypeResolver;

  @Autowired
  private SizeFunction sizeFunction;

  private FilterJsonNodeTransformer transformer;

  @BeforeEach
  void init() {
    transformer = new FilterJsonNodeTransformer(conversionService, objectMapper,
        filterNodeProcessorFactories, fieldTypeResolver, TestEntity.class);
  }

  private void test(String expectedJson, FilterNode filterNode) {
    JsonNode expectedOutput = objectMapper.readTree(expectedJson);
    Assertions.assertEquals(expectedOutput.toString(),
        transformer
            .transform(filterNode)
            .toString());
  }

  @Test
  void equalTest() {
    test("""
            {
              "$eq": [
                "$string",
                "hello"
              ]
            }
            """,
        fb
            .field("string")
            .equal(fb.input("hello"))
            .get());
  }

  @Test
  void notEqualTest() {
    test("""
            {
              "$ne": [
                "$string",
                "hello"
              ]
            }
            """,
        fb
            .field("string")
            .notEqual(fb.input("hello"))
            .get());
  }

  @Test
  void nestedEntityTest() {
    test("""
            {
              "$eq" : [
                "$nested.field",
                "value"
              ]
            }
            """,
        fb
            .field("nested.field")
            .equal(fb.input("value"))
            .get());
  }

  @Test
  void betweenTest() {
    test("""
            {
              "$and": [
                { "$gte": ["$integer", 10] },
                { "$lte": ["$integer", 20] }
              ]
            }
            """,
        fb
            .field("integer")
            .between(fb.input(10), fb.input(20))
            .get());
  }

  @Test
  void likeCollectionTest() {
    test("""
            {
              "$or": [
                { "$string": { "$regex": "hello%" } },
                { "$string": { "$regex": "%world" } }
              ]
            }
            """,
        fb
            .field("string")
            .likeCollection(fb.input("hello%"), fb.input("%world"))
            .get());
  }

  @Test
  void xorTest() {
    test("""
            {
              "$xor": [
                { "$gt": ["$integer", 15] },
                { "$lte": ["$integer", 25] }
              ]
            }
            """,
        fb.field("integer").greaterThan(fb.input(15))
            .xor(fb.field("integer").lessThanOrEqual(fb.input(25))).get());
  }

  @Test
  void mapKeyEqualityTest() {
    test("""
            {
              "$eq": [
                "$metadata.someKey",
                "someValue"
              ]
            }
            """,
        fb
            .field("metadata.someKey")
            .equal(fb.input("someValue"))
            .get());
  }

  @Test
  void mapValueCoercionTest() {
    test("""
            {
              "$eq": [
                "$counters.views",
                5
              ]
            }
            """,
        fb
            .field("counters.views")
            .equal(fb.input("5"))
            .get());
  }

  @Test
  void mapValueObjectTest() {
    test("""
            {
              "$eq": [
                "$nestedByName.alice.field",
                "value"
              ]
            }
            """,
        fb
            .field("nestedByName.alice.field")
            .equal(fb.input("value"))
            .get());
  }

  @Test
  void nestedMapTest() {
    test("""
            {
              "$eq": [
                "$nestedMaps.outer.inner",
                "x"
              ]
            }
            """,
        fb
            .field("nestedMaps.outer.inner")
            .equal(fb.input("x"))
            .get());
  }

  @Test
  void mapKeyLikeTest() {
    test("""
            {
              "$regexMatch": {
                "input": "$metadata.someKey",
                "regex": ".*some.*",
                "options": ""
              }
            }
            """,
        fb
            .field("metadata.someKey")
            .like(fb.input("*some*"))
            .get());
  }

  @Test
  void mapKeyCollidingWithValueFieldNameTest() {
    test("""
            {
              "$eq": [
                "$nestedByName.field.field",
                "value"
              ]
            }
            """,
        fb
            .field("nestedByName.field.field")
            .equal(fb.input("value"))
            .get());
  }

  @Test
  void mapFieldItselfIsNullTest() {
    test("""
            {
              "$lte": [
                "$metadata",
                null
              ]
            }
            """,
        fb
            .field("metadata")
            .isNull()
            .get());
  }

  @Test
  void mapKeyInTest() {
    test("""
            {
              "$and": [
                { "$isArray": [[5, 7]] },
                { "$in": ["$counters.views", [5, 7]] }
              ]
            }
            """,
        fb
            .field("counters.views")
            .in(fb.collection(fb.input("5"), fb.input(7)))
            .get());
  }

  @Test
  void mapOfListInDoesNotNestArraysTest() {
    test("""
            {
              "$and": [
                { "$isArray": [["a", "b"]] },
                { "$in": ["$tags.someKey", ["a", "b"]] }
              ]
            }
            """,
        fb
            .field("tags.someKey")
            .in(fb.collection(fb.input("a"), fb.input("b")))
            .get());
  }

  @Test
  void mapOfListEqualityKeepsScalarInputTest() {
    test("""
            {
              "$eq": [
                "$tags.someKey",
                "red"
              ]
            }
            """,
        fb
            .field("tags.someKey")
            .equal(fb.input("red"))
            .get());
  }

  @Test
  void enumMapValueCoercionTest() {
    test("""
            {
              "$eq": [
                "$statuses.someKey",
                "ACTIVE"
              ]
            }
            """,
        fb
            .field("statuses.someKey")
            .equal(fb.input("ACTIVE"))
            .get());
  }

  @Test
  void booleanMapValueCoercionTest() {
    test("""
            {
              "$eq": [
                "$flags.someKey",
                true
              ]
            }
            """,
        fb
            .field("flags.someKey")
            .equal(fb.input("true"))
            .get());
  }

  @Test
  void mapValueBetweenCoercesBoundsTest() {
    test("""
            {
              "$and": [
                { "$gte": ["$counters.views", 5] },
                { "$lte": ["$counters.views", 10] }
              ]
            }
            """,
        fb
            .field("counters.views")
            .between(fb.input("5"), fb.input("10"))
            .get());
  }

  @Test
  void insensitiveLikeOnMapKeyTest() {
    test("""
            {
              "$regexMatch": {
                "input": "$metadata.someKey",
                "regex": ".*some.*",
                "options": "i"
              }
            }
            """,
        fb
            .field("metadata.someKey")
            .insensitiveLike(fb.input("*some*"))
            .get());
  }

  @Test
  void sizeOnArrayFieldUnchangedTest() {
    test("""
            {
              "$size": "$integers"
            }
            """,
        fb
            .function(sizeFunction, fb.field("integers"))
            .get());
  }

  @Test
  void sizeOnMapFieldUsesObjectToArrayTest() {
    test("""
            {
              "$size": { "$ifNull": [ { "$objectToArray": "$metadata" }, [] ] }
            }
            """,
        fb
            .function(sizeFunction, fb.field("metadata"))
            .get());
  }

  @Test
  void sizeOnMapValuePathUnchangedTest() {
    test("""
            {
              "$size": "$metadata.someKey"
            }
            """,
        fb
            .function(sizeFunction, fb.field("metadata.someKey"))
            .get());
  }

  @Test
  void isEmptyOnArrayFieldUnchangedTest() {
    test("""
            {
              "$and": [
                { "$isArray": "$integers" },
                { "$eq": [ { "$size": "$integers" }, 0 ] }
              ]
            }
            """,
        fb
            .field("integers")
            .isEmpty()
            .get());
  }

  @Test
  void isEmptyOnMapFieldUsesObjectToArrayTest() {
    test("""
            {
              "$and": [
                { "$isArray": { "$ifNull": [ { "$objectToArray": "$metadata" }, [] ] } },
                { "$eq": [
                    { "$size": { "$ifNull": [ { "$objectToArray": "$metadata" }, [] ] } },
                    0
                ] }
              ]
            }
            """,
        fb
            .field("metadata")
            .isEmpty()
            .get());
  }

  @Test
  void isNotEmptyOnMapFieldUsesObjectToArrayTest() {
    test("""
            {
              "$and": [
                { "$isArray": { "$ifNull": [ { "$objectToArray": "$metadata" }, [] ] } },
                { "$gt": [
                    { "$size": { "$ifNull": [ { "$objectToArray": "$metadata" }, [] ] } },
                    0
                ] }
              ]
            }
            """,
        fb
            .field("metadata")
            .isNotEmpty()
            .get());
  }

  @Test
  void hashMapKeyNamedSizeStaysStringTest() {
    test("""
            {
              "$eq": [
                "$hashMapWebsites.size",
                "5"
              ]
            }
            """,
        fb
            .field("hashMapWebsites.size")
            .equal(fb.input("5"))
            .get());
  }

  @Test
  void mapKeyNotInTest() {
    test("""
            {
              "$and": [
                { "$isArray": [5, 7] },
                { "$not": { "$in": ["$counters.views", [5, 7]] } }
              ]
            }
            """,
        fb
            .field("counters.views")
            .notIn(fb.collection(fb.input("5"), fb.input(7)))
            .get());
  }

}
