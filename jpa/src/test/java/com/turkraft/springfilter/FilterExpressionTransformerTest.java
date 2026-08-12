package com.turkraft.springfilter;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.definition.FilterFunction;
import com.turkraft.springfilter.helper.ExistsExpressionHelper;
import com.turkraft.springfilter.helper.PathExpressionHelper;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
public class FilterExpressionTransformerTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private ConversionService conversionService;

  @Autowired
  private FilterBuilder fb;

  @Autowired
  private PathExpressionHelper pathExpressionHelper;

  @Autowired
  private ExistsExpressionHelper existsExpressionHelper;

  @Autowired
  private FilterNodeProcessorFactories filterNodeProcessorFactories;

  @Autowired
  @Qualifier("anyFunction")
  private FilterFunction anyFunction;

  @Autowired
  @Qualifier("allFunction")
  private FilterFunction allFunction;

  @Autowired
  @Qualifier("someFunction")
  private FilterFunction someFunction;

  @Autowired
  private com.turkraft.springfilter.language.JsonTextFunction jsonTextFunction;

  @Autowired
  private com.turkraft.springfilter.language.ToIntegerFunction toIntegerFunction;

  private CriteriaQuery<TestEntity> criteriaQuery;

  private Root<TestEntity> root;

  private FilterExpressionTransformer transformer;

  @BeforeEach
  void initEach() {
    entityManager
        .createNativeQuery(
            "CREATE ALIAS IF NOT EXISTS jsonb_extract_path_text FOR \"com.turkraft.springfilter.H2JsonHelper.jsonbExtractPathText\"")
        .executeUpdate();
    criteriaQuery = entityManager
        .getCriteriaBuilder()
        .createQuery(TestEntity.class);
    root = criteriaQuery.from(TestEntity.class);
    transformer = new FilterExpressionTransformer(conversionService,
        pathExpressionHelper,
        existsExpressionHelper,
        filterNodeProcessorFactories,
        root, criteriaQuery, entityManager.getCriteriaBuilder());
  }

  private void test(String expectedSql, FilterNode filterNode) {

    criteriaQuery
        .select(root)
        .where((Expression<Boolean>) transformer.transform(filterNode));
    TypedQuery<TestEntity> query = entityManager.createQuery(criteriaQuery);
    List<TestEntity> transformerResults = query.getResultList();

    List<TestEntity> expectedResults = entityManager
        .createQuery(expectedSql, TestEntity.class)
        .getResultList();

    Assertions.assertFalse(expectedResults.isEmpty(), "Queries should return at least one result");

    Assertions.assertEquals(expectedResults.size(), transformerResults.size());

    for (int i = 0; i < expectedResults.size(); i++) {
      Assertions.assertEquals(expectedResults
              .get(i)
              .getId(),
          transformerResults
              .get(i)
              .getId());
    }

  }

  private TestEntity createEntity(int integerValue, List<Integer> integersList) {
    TestEntity e = new TestEntity();
    e.setInteger(integerValue);
    e.setIntegers(integersList);
    entityManager.persist(e);
    return e;
  }

  @Test
  void equalTest() {

    TestEntity e1 = new TestEntity();
    e1.setString("hello world");
    entityManager.persist(e1);

    TestEntity e2 = new TestEntity();
    e2.setString("xyz");
    entityManager.persist(e2);

    test("""
            select t from TestEntity t where t.string = 'hello world'
            """,
        fb
            .field("string")
            .equal(fb.input("hello world"))
            .get());

  }

  @Test
  void notEqualTest() {

    TestEntity e1 = new TestEntity();
    e1.setString("hello world");
    entityManager.persist(e1);

    TestEntity e2 = new TestEntity();
    e2.setString("xyz");
    entityManager.persist(e2);

    test("""
            select t from TestEntity t where t.string != 'hello world'
            """,
        fb
            .field("string")
            .notEqual(fb.input("hello world"))
            .get());

  }

  @Test
  void anyGreaterThanTest() {
    createEntity(10, Arrays.asList(5, 10, 20));
    createEntity(3, Arrays.asList(5, 10, 15));
    createEntity(25, Arrays.asList(5, 10, 15));

    test("""
            select t from TestEntity t where t.integer > any (select i from t.integers i)
            """,
        fb
            .field("integer")
            .greaterThan(fb.function(anyFunction, fb.field("integers")))
            .get());
  }

  @Test
  void anyEqualToTest() {
    createEntity(10, Arrays.asList(5, 10, 20));
    createEntity(3, Arrays.asList(5, 10, 15));
    createEntity(25, Arrays.asList(5, 10, 15));

    test("""
            select t from TestEntity t where t.integer = any (select i from t.integers i)
            """,
        fb
            .field("integer")
            .equal(fb.function(anyFunction, fb.field("integers")))
            .get());
  }

  @Test
  void someGreaterThanTest() {
    createEntity(10, Arrays.asList(5, 10, 20));
    createEntity(3, Arrays.asList(5, 10, 15));
    createEntity(25, Arrays.asList(5, 10, 15));

    test("""
            select t from TestEntity t where t.integer > some (select i from t.integers i)
            """,
        fb
            .field("integer")
            .greaterThan(fb.function(someFunction, fb.field("integers")))
            .get());
  }

  @Test
  void allGreaterThanTest() {
    createEntity(10, Arrays.asList(15, 20, 25));
    createEntity(30, Arrays.asList(15, 20, 25));
    createEntity(5, Arrays.asList(1, 2, 3));

    test("""
            select t from TestEntity t where t.integer > all (select i from t.integers i)
            """,
        fb
            .field("integer")
            .greaterThan(fb.function(allFunction, fb.field("integers")))
            .get());
  }

  @Test
  void allLessThanOrEqualToTest() {
    createEntity(10, Arrays.asList(15, 20, 25));
    createEntity(30, Arrays.asList(15, 20, 25));
    createEntity(5, Arrays.asList(1, 2, 3));

    test("""
            select t from TestEntity t where t.integer <= all (select i from t.integers i)
            """,
        fb
            .field("integer")
            .lessThanOrEqual(fb.function(allFunction, fb.field("integers")))
            .get());
  }

  @Test
  void anyWithEmptyCollectionTest() {
    createEntity(10, Collections.emptyList());
    createEntity(3, Arrays.asList(5, 10, 15));
    createEntity(25, Arrays.asList(5, 10, 15));

    test("""
            select t from TestEntity t where t.integer > any (select i from t.integers i)
            """,
        fb
            .field("integer")
            .greaterThan(fb.function(anyFunction, fb.field("integers")))
            .get());
  }

  @Test
  void allWithEmptyCollectionTest() {
    createEntity(10, Collections.emptyList());
    createEntity(3, Arrays.asList(5, 10, 15));
    createEntity(25, Arrays.asList(5, 10, 15));

    test("""
            select t from TestEntity t where t.integer > all (select i from t.integers i)
            """,
        fb
            .field("integer")
            .greaterThan(fb.function(allFunction, fb.field("integers")))
            .get());
  }

  @Test
  void allNotEqualToTest() {
    createEntity(10, Arrays.asList(10, 20, 30));
    createEntity(15, Arrays.asList(10, 20, 30));
    createEntity(25, Arrays.asList(10, 20, 30));

    test("""
            select t from TestEntity t where t.integer != all (select i from t.integers i)
            """,
        fb
            .field("integer")
            .notEqual(fb.function(allFunction, fb.field("integers")))
            .get());
  }

  @Test
  void parseRoundTripAnyTest() {
    FilterNode node = fb
        .field("integer")
        .greaterThan(fb.function(anyFunction, fb.field("integers")))
        .get();
    Assertions.assertNotNull(node);
    String converted = conversionService.convert(node, String.class);
    Assertions.assertNotNull(converted);
    Assertions.assertTrue(converted.contains("any(integers)"));
  }

  @Test
  void betweenTest() {
    createEntity(5, Collections.emptyList());
    createEntity(10, Collections.emptyList());
    createEntity(15, Collections.emptyList());
    createEntity(20, Collections.emptyList());
    createEntity(25, Collections.emptyList());

    test("""
            select t from TestEntity t where t.integer >= 10 and t.integer <= 20
            """,
        fb
            .field("integer")
            .between(fb.input(10), fb.input(20))
            .get());
  }

  @Test
  void betweenParseTest() {
    createEntity(5, Collections.emptyList());
    createEntity(10, Collections.emptyList());
    createEntity(15, Collections.emptyList());
    createEntity(20, Collections.emptyList());
    createEntity(25, Collections.emptyList());

    FilterNode node = conversionService.convert("integer between 10 and 20", FilterNode.class);

    test("""
            select t from TestEntity t where t.integer >= 10 and t.integer <= 20
            """,
        node);
  }

  @Test
  void likeCollectionTest() {
    TestEntity e1 = new TestEntity();
    e1.setString("hello world");
    entityManager.persist(e1);

    TestEntity e2 = new TestEntity();
    e2.setString("foo bar");
    entityManager.persist(e2);

    TestEntity e3 = new TestEntity();
    e3.setString("xyz");
    entityManager.persist(e3);

    test("""
            select t from TestEntity t where t.string like 'hello%' or t.string like '%bar'
            """,
        fb
            .field("string")
            .likeCollection(fb.input("hello%"), fb.input("%bar"))
            .get());
  }

  @Test
  void likeCollectionParseTest() {
    TestEntity e1 = new TestEntity();
    e1.setString("hello world");
    entityManager.persist(e1);

    TestEntity e2 = new TestEntity();
    e2.setString("foo bar");
    entityManager.persist(e2);

    TestEntity e3 = new TestEntity();
    e3.setString("xyz");
    entityManager.persist(e3);

    FilterNode node = conversionService.convert("string ~ ['hello%', '%bar']", FilterNode.class);

    test("""
            select t from TestEntity t where t.string like 'hello%' or t.string like '%bar'
            """,
        node);
  }

  @Test
  void xorTest() {
    createEntity(10, Collections.emptyList());
    createEntity(20, Collections.emptyList());
    createEntity(30, Collections.emptyList());

    test("""
            select t from TestEntity t where (t.integer > 15 and not (t.integer <= 25)) or (not (t.integer > 15) and t.integer <= 25)
            """,
        fb.field("integer").greaterThan(fb.input(15))
            .xor(fb.field("integer").lessThanOrEqual(fb.input(25))).get());
  }

  @Test
  void isEmptyOrCollectionEqualTest() {
    createEntity(10, Collections.emptyList());
    createEntity(10, Collections.singletonList(5));
    createEntity(10, Arrays.asList(1, 2, 3));
    createEntity(5, Collections.emptyList());
    createEntity(5, Collections.singletonList(5));

    test("""
            select t from TestEntity t where t.integer = 10 and (t.integers is empty or 5 member of t.integers)
            """,
        fb.field("integer").equal(fb.input(10))
            .and(
                fb.field("integers").isEmpty()
                    .or(fb.field("integers").equal(fb.input(5)))
            ).get());
  }

  @Test
  void collectionEqualTest() {
    createEntity(10, Collections.singletonList(5));
    createEntity(10, Arrays.asList(1, 2, 3));
    createEntity(10, Arrays.asList(4, 5, 6));

    test("""
            select t from TestEntity t where 5 member of t.integers
            """,
        fb.field("integers").equal(fb.input(5)).get());
  }

  @Test
  void isNotEmptyAndCollectionNotEqualTest() {
    createEntity(10, Collections.singletonList(5));
    createEntity(10, Collections.singletonList(7));
    createEntity(10, Collections.emptyList());
    createEntity(10, Arrays.asList(1, 2));
    createEntity(10, Arrays.asList(5, 7));

    test("""
            select t from TestEntity t where t.integers is not empty and exists(select 1 from t.integers i where i <> 5)
            """,
        fb.field("integers").isNotEmpty()
            .and(fb.field("integers").notEqual(fb.input(5))).get());
  }

  private TestEntity createEntityWithJson(String jsonData) {
    TestEntity e = new TestEntity();
    e.setJsonData(jsonData);
    entityManager.persist(e);
    return e;
  }

  @Test
  void jsonTextEqualTest() {
    createEntityWithJson("{\"name\": \"John\", \"age\": 30}");
    createEntityWithJson("{\"name\": \"Jane\", \"age\": 25}");
    createEntityWithJson("{\"city\": \"Paris\"}");

    test("""
            select t from TestEntity t where function('jsonb_extract_path_text', t.jsonData, 'name') = 'John'
            """,
        fb.function(jsonTextFunction, fb.field("jsonData"), fb.input("name"))
            .equal(fb.input("John")).get());
  }

  @Test
  void jsonTextIsNullTest() {
    createEntityWithJson("{\"name\": \"John\"}");
    createEntityWithJson("{\"age\": 30}");

    test("""
            select t from TestEntity t where function('jsonb_extract_path_text', t.jsonData, 'name') is null
            """,
        fb.function(jsonTextFunction, fb.field("jsonData"), fb.input("name"))
            .isNull().get());
  }

  @Test
  void jsonTextWithToIntegerTest() {
    createEntityWithJson("{\"name\": \"John\", \"age\": 30}");
    createEntityWithJson("{\"name\": \"Jane\", \"age\": 15}");
    createEntityWithJson("{\"name\": \"Bob\"}");

    test("""
            select t from TestEntity t where cast(function('jsonb_extract_path_text', t.jsonData, 'age') as integer) > 18
            """,
        fb.function(toIntegerFunction,
                fb.function(jsonTextFunction, fb.field("jsonData"), fb.input("age")))
            .greaterThan(fb.input(18)).get());
  }

  @Test
  void jsonTextNestedPathTest() {
    createEntityWithJson("{\"address\": {\"city\": \"Paris\"}}");
    createEntityWithJson("{\"address\": {\"city\": \"London\"}}");
    createEntityWithJson("{\"name\": \"John\"}");

    test("""
            select t from TestEntity t where function('jsonb_extract_path_text', t.jsonData, 'address', 'city') = 'Paris'
            """,
        fb.function(jsonTextFunction, fb.field("jsonData"), fb.input("address"), fb.input("city"))
            .equal(fb.input("Paris")).get());
  }

}