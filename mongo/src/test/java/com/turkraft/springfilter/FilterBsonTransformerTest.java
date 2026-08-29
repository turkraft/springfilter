package com.turkraft.springfilter;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.language.HelloWorldPlaceholder;
import com.turkraft.springfilter.language.SizeFunction;
import com.turkraft.springfilter.language.TodayFunction;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.convert.Jsr310Converters;

@ExtendWith(SpringExtension.class)
public class FilterBsonTransformerTest {

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
  private FieldTypeResolver fieldTypeResolver;

  @Autowired
  private SizeFunction sizeFunction;

  @Autowired
  private TodayFunction todayFunction;

  @Autowired
  private HelloWorldPlaceholder helloWorldPlaceholder;

  private FilterBsonTransformer transformer;

  @BeforeEach
  void init() {
    transformer = new FilterBsonTransformer(conversionService, filterNodeProcessorFactories,
        fieldTypeResolver, TestEntity.class);
  }

  private void test(String expectedJson, FilterNode filterNode) {
    // Extended JSON keeps the expectations readable: $oid and $date parse to ObjectId and Date.
    test(Document.parse("{\"r\": " + expectedJson + "}").get("r"), filterNode);
  }

  private void test(Object expectedOutput, FilterNode filterNode) {
    Assertions.assertEquals(expectedOutput, transformer.transform(filterNode));
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
                { "$regexMatch": { "input": "$string", "regex": ".*hello%.*", "options": "" } },
                { "$regexMatch": { "input": "$string", "regex": ".*%world.*", "options": "" } }
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
              "$ne": [
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
              "$size": { "$objectToArray": "$metadata" }
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
                { "$isArray": { "$objectToArray": "$metadata" } },
                { "$eq": [ { "$size": { "$objectToArray": "$metadata" } }, 0 ] }
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
                { "$isArray": { "$objectToArray": "$metadata" } },
                { "$gt": [ { "$size": { "$objectToArray": "$metadata" } }, 0 ] }
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
                { "$isArray": [[5, 7]] },
                { "$not": { "$in": ["$counters.views", [5, 7]] } }
              ]
            }
            """,
        fb
            .field("counters.views")
            .notIn(fb.collection(fb.input("5"), fb.input(7)))
            .get());
  }

  @Test
  void dbrefIdEqualityOnSingleReferenceTest() {
    test("""
            {
              "$eq": [
                "$manager.$id",
                { "$oid": "642ebb0e91ac8f778f5654b7" }
              ]
            }
            """,
        fb
            .field("manager.$id")
            .equal(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void dbrefRefEqualityOnSingleReferenceTest() {
    test("""
            {
              "$eq": [
                "$manager.$ref",
                "referencedTestEntity"
              ]
            }
            """,
        fb
            .field("manager.$ref")
            .equal(fb.input("referencedTestEntity"))
            .get());
  }

  @Test
  void dbrefIdNotEqualOnSingleReferenceTest() {
    test("""
            {
              "$ne": [
                "$manager.$id",
                { "$oid": "642ebb0e91ac8f778f5654b7" }
              ]
            }
            """,
        fb
            .field("manager.$id")
            .notEqual(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void dbrefIdEqualityOnCollectionUsesMembershipTest() {
    test("""
            {
              "$in": [
                { "$oid": "642ebb0e91ac8f778f5654b7" },
                { "$ifNull": [ "$roles.$id", [] ] }
              ]
            }
            """,
        fb
            .field("roles.$id")
            .equal(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void dbrefIdNotEqualOnCollectionUsesNegatedMembershipTest() {
    test("""
            {
              "$not": {
                "$in": [
                  { "$oid": "642ebb0e91ac8f778f5654b7" },
                  { "$ifNull": [ "$roles.$id", [] ] }
                ]
              }
            }
            """,
        fb
            .field("roles.$id")
            .notEqual(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void dbrefIdInOnCollectionUsesAnyElementTrueTest() {
    test("""
            {
              "$anyElementTrue": [
                { "$map": {
                  "input": { "$ifNull": [ "$roles.$id", [] ] },
                  "in": { "$in": [ "$$this", [ { "$oid": "642ebb0e91ac8f778f5654b7" }, { "$oid": "642ebb0e91ac8f778f5654b8" } ] ] }
                } }
              ]
            }
            """,
        fb
            .field("roles.$id")
            .in(fb.collection(fb.input("642ebb0e91ac8f778f5654b7"),
                fb.input("642ebb0e91ac8f778f5654b8")))
            .get());
  }

  @Test
  void dbrefIdNotInOnCollectionUsesNegatedAnyElementTrueTest() {
    test("""
            {
              "$not": {
                "$anyElementTrue": [
                  { "$map": {
                    "input": { "$ifNull": [ "$roles.$id", [] ] },
                    "in": { "$in": [ "$$this", [ { "$oid": "642ebb0e91ac8f778f5654b7" } ] ] }
                  } }
                ]
              }
            }
            """,
        fb
            .field("roles.$id")
            .notIn(fb.collection(fb.input("642ebb0e91ac8f778f5654b7")))
            .get());
  }

  @Test
  void dbrefIdIsNullOnSingleReferenceTest() {
    test("""
            {
              "$lte": [
                "$manager.$id",
                null
              ]
            }
            """,
        fb
            .field("manager.$id")
            .isNull()
            .get());
  }

  @Test
  void dollarSegmentAsMapKeyUsesPlainPathTest() {
    test("""
            {
              "$eq": [
                "$metadata.$id",
                "someValue"
              ]
            }
            """,
        fb
            .field("metadata.$id")
            .equal(fb.input("someValue"))
            .get());
  }

  @Test
  void leadingDollarSegmentUsesGetFieldTest() {
    test("""
            {
              "$eq": [
                { "$getField": { "field": { "$literal": "$id" }, "input": "$$ROOT" } },
                "someValue"
              ]
            }
            """,
        fb
            .field("$id")
            .equal(fb.input("someValue"))
            .get());
  }

  @Test
  void leadingDollarSegmentWithSubPathChainsGetFieldTest() {
    test("""
            {
              "$eq": [
                { "$getField": {
                  "field": "name",
                  "input": { "$getField": { "field": { "$literal": "$id" }, "input": "$$ROOT" } }
                } },
                "someValue"
              ]
            }
            """,
        fb
            .field("$id.name")
            .equal(fb.input("someValue"))
            .get());
  }

  @Test
  void dbrefIdGreaterThanOnCollectionUsesAnyElementTrueTest() {
    test("""
            {
              "$anyElementTrue": [
                { "$map": {
                  "input": { "$ifNull": [ "$roles.$id", [] ] },
                  "in": { "$gt": [ "$$this", { "$oid": "642ebb0e91ac8f778f5654b7" } ] }
                } }
              ]
            }
            """,
        fb
            .field("roles.$id")
            .greaterThan(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void dbrefIdLessThanOrEqualOnCollectionUsesAnyElementTrueTest() {
    test("""
            {
              "$anyElementTrue": [
                { "$map": {
                  "input": { "$ifNull": [ "$roles.$id", [] ] },
                  "in": { "$lte": [ "$$this", { "$oid": "642ebb0e91ac8f778f5654b7" } ] }
                } }
              ]
            }
            """,
        fb
            .field("roles.$id")
            .lessThanOrEqual(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void dbrefIdGreaterThanOnSingleReferenceUsesPlainComparisonTest() {
    test("""
            {
              "$gt": [
                "$manager.$id",
                { "$oid": "642ebb0e91ac8f778f5654b7" }
              ]
            }
            """,
        fb
            .field("manager.$id")
            .greaterThan(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void dbrefIdLikeOnCollectionUsesAnyElementTrueTest() {
    test("""
            {
              "$anyElementTrue": [
                { "$map": {
                  "input": { "$ifNull": [ "$roles.$id", [] ] },
                  "in": { "$regexMatch": {
                    "input": { "$convert": {
                      "input": "$$this", "to": "string", "onError": "", "onNull": ""
                    } },
                    "regex": ".*abc.*",
                    "options": ""
                  } }
                } }
              ]
            }
            """,
        fb
            .field("roles.$id")
            .like(fb.input("abc"))
            .get());
  }

  @Test
  void dbrefIdLikeOnSingleReferenceStringifiesInputTest() {
    test("""
            {
              "$regexMatch": {
                "input": { "$convert": {
                  "input": "$manager.$id", "to": "string", "onError": "", "onNull": ""
                } },
                "regex": ".*abc.*",
                "options": ""
              }
            }
            """,
        fb
            .field("manager.$id")
            .like(fb.input("abc"))
            .get());
  }

  @Test
  void rootIdFieldMapsToUnderscoreIdTest() {
    test("""
            {
              "$eq": [
                "$_id",
                { "$oid": "642ebb0e91ac8f778f5654b7" }
              ]
            }
            """,
        fb
            .field("id")
            .equal(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void nestedReferenceIdFieldDoesNotMapToRootUnderscoreIdTest() {
    test("""
            {
              "$eq": [
                "$manager.id",
                { "$oid": "642ebb0e91ac8f778f5654b7" }
              ]
            }
            """,
        fb
            .field("manager.id")
            .equal(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void documentReferenceIdEqualityOnSingleReferenceDropsIdSegmentTest() {
    test("""
            {
              "$eq": [
                "$advisor",
                { "$oid": "642ebb0e91ac8f778f5654b7" }
              ]
            }
            """,
        fb
            .field("advisor.$id")
            .equal(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void documentReferenceIdEqualityOnCollectionUsesMembershipTest() {
    test("""
            {
              "$in": [
                { "$oid": "642ebb0e91ac8f778f5654b7" },
                { "$ifNull": [ "$teams", [] ] }
              ]
            }
            """,
        fb
            .field("teams.$id")
            .equal(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void documentReferenceIdInOnCollectionUsesAnyElementTrueTest() {
    test("""
            {
              "$anyElementTrue": [
                { "$map": {
                  "input": { "$ifNull": [ "$teams", [] ] },
                  "in": { "$in": [ "$$this", [ { "$oid": "642ebb0e91ac8f778f5654b7" } ] ] }
                } }
              ]
            }
            """,
        fb
            .field("teams.$id")
            .in(fb.collection(fb.input("642ebb0e91ac8f778f5654b7")))
            .get());
  }

  // Typed values: the BSON transformer must never fall back to strings for non-string fields.

  @Test
  void instantInputBecomesDate() {
    test(new Document("$eq", Arrays.asList("$instant",
            Date.from(Instant.parse("2023-01-01T00:00:00Z")))),
        fb
            .field("instant")
            .equal(fb.input("2023-01-01T00:00:00Z"))
            .get());
  }

  @Test
  void instantInputMatchesExtendedJsonDate() {
    test("""
            {
              "$gt": [
                "$instant",
                { "$date": "2023-01-01T00:00:00Z" }
              ]
            }
            """,
        fb
            .field("instant")
            .greaterThan(fb.input("2023-01-01T00:00:00Z"))
            .get());
  }

  @Test
  void localDateInputBecomesDateAtStartOfDay() {
    test(new Document("$eq", Arrays.asList("$localDate",
            Jsr310Converters.LocalDateToDateConverter.INSTANCE.convert(LocalDate.of(2023, 1, 1)))),
        fb
            .field("localDate")
            .equal(fb.input("2023-01-01"))
            .get());
  }

  @Test
  void localDateTimeInputBecomesDate() {
    LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 12, 30);
    test(new Document("$lt", Arrays.asList("$localDateTime",
            Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()))),
        fb
            .field("localDateTime")
            .lessThan(fb.input("2023-01-01T12:30:00"))
            .get());
  }

  @Test
  void temporalCollectionItemsBecomeDates() {
    test(new Document("$and", Arrays.asList(
            new Document("$isArray", Arrays.asList(Arrays.asList(
                Date.from(Instant.parse("2020-06-01T00:00:00Z")),
                Date.from(Instant.parse("2026-06-01T00:00:00Z"))))),
            new Document("$in", Arrays.asList("$instants", Arrays.asList(
                Date.from(Instant.parse("2020-06-01T00:00:00Z")),
                Date.from(Instant.parse("2026-06-01T00:00:00Z"))))))),
        fb
            .field("instants")
            .in(fb.collection(fb.input("2020-06-01T00:00:00Z"), fb.input("2026-06-01T00:00:00Z")))
            .get());
  }

  @Test
  void stringIdInputBecomesObjectId() {
    test(new Document("$eq",
            Arrays.asList("$_id", new ObjectId("642ebb0e91ac8f778f5654b7"))),
        fb
            .field("id")
            .equal(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void objectIdFieldInputBecomesObjectId() {
    test(new Document("$eq",
            Arrays.asList("$objectId", new ObjectId("642ebb0e91ac8f778f5654b7"))),
        fb
            .field("objectId")
            .equal(fb.input("642ebb0e91ac8f778f5654b7"))
            .get());
  }

  @Test
  void uuidInputBecomesUuid() {
    UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    test(new Document("$eq", Arrays.asList("$uuid", uuid)),
        fb
            .field("uuid")
            .equal(fb.input(uuid.toString()))
            .get());
    test(new Document("$eq", Arrays.asList("$uuidByName.home", uuid)),
        fb
            .field("uuidByName.home")
            .equal(fb.input(uuid.toString()))
            .get());
  }

  @Test
  void numericInputIsCoercedToTheFieldType() {
    test(new Document("$eq", Arrays.asList("$integer", 5)),
        fb
            .field("integer")
            .equal(fb.input("5"))
            .get());
    test(new Document("$eq", Arrays.asList("$amount", new BigDecimal("12.50"))),
        fb
            .field("amount")
            .equal(fb.input("12.50"))
            .get());
  }

  @Test
  void enumInputBecomesItsName() {
    test(new Document("$eq", Arrays.asList("$status", "ACTIVE")),
        fb
            .field("status")
            .equal(fb.input("ACTIVE"))
            .get());
  }

  @Test
  void isNullKeepsTheNullOperand() {
    test(new Document("$lte", Arrays.asList("$instant", null)),
        fb
            .field("instant")
            .isNull()
            .get());
    test(new Document("$gt", Arrays.asList("$instant", null)),
        fb
            .field("instant")
            .isNotNull()
            .get());
  }

  @Test
  void likeOnStringIdConvertsTheIdentifierToString() {
    test("""
            {
              "$regexMatch": {
                "input": { "$convert": {
                  "input": "$_id", "to": "string", "onError": "", "onNull": ""
                } },
                "regex": ".*abc.*",
                "options": ""
              }
            }
            """,
        fb
            .field("id")
            .like(fb.input("abc"))
            .get());
  }

  @Test
  void likeOnObjectIdFieldConvertsTheIdentifierToString() {
    test("""
            {
              "$regexMatch": {
                "input": { "$convert": {
                  "input": "$objectId", "to": "string", "onError": "", "onNull": ""
                } },
                "regex": ".*abc.*",
                "options": "i"
              }
            }
            """,
        fb
            .field("objectId")
            .insensitiveLike(fb.input("abc"))
            .get());
  }

  @Test
  void insensitiveLikeCollectionUsesRegexMatchWithOptions() {
    test("""
            {
              "$regexMatch": { "input": "$string", "regex": ".*hello.*", "options": "i" }
            }
            """,
        fb
            .field("string")
            .insensitiveLikeCollection(fb.input("hello"))
            .get());
  }

  @Test
  void todayIsAStartOfDayDate() {
    Object result = transformer.transform(fb.function(todayFunction).get());
    Assertions.assertInstanceOf(Date.class, result);
    Assertions.assertEquals(
        Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), result);
  }

  @Test
  void placeholderProducesItsValue() {
    Assertions.assertEquals("Hello world!",
        transformer.transform(fb.placeholder(helloWorldPlaceholder).get()));
  }

  @Test
  void producedTreeContainsOnlyDriverEncodableValues() {
    Object result = transformer.transform(fb
        .field("instant").greaterThan(fb.input("2023-01-01T00:00:00Z"))
        .and(fb.field("id").in(fb.collection(fb.input("642ebb0e91ac8f778f5654b7"))))
        .and(fb.field("uuid").equal(fb.input("550e8400-e29b-41d4-a716-446655440000")))
        .and(fb.field("status").equal(fb.input("ACTIVE")))
        .get());
    assertDriverEncodable(result);
  }

  private static void assertDriverEncodable(Object value) {
    if (value instanceof Document document) {
      document.values().forEach(FilterBsonTransformerTest::assertDriverEncodable);
    } else if (value instanceof List<?> list) {
      list.forEach(FilterBsonTransformerTest::assertDriverEncodable);
    } else if (value != null) {
      Assertions.assertTrue(value instanceof String || value instanceof Boolean
              || value instanceof Number || value instanceof Date || value instanceof ObjectId
              || value instanceof UUID,
          "Unexpected value type in BSON tree: " + value.getClass());
    }
  }

}
