package com.turkraft.springfilter;

import com.turkraft.springfilter.builder.FilterBuilder;
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
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

  private CriteriaQuery<TestEntity> criteriaQuery;

  private Root<TestEntity> root;

  private FilterExpressionTransformer transformer;

  @BeforeEach
  void initEach() {
    criteriaQuery = entityManager.getCriteriaBuilder().createQuery(TestEntity.class);
    root = criteriaQuery.from(TestEntity.class);
    transformer = new FilterExpressionTransformer(conversionService,
        pathExpressionHelper,
        existsExpressionHelper,
        filterNodeProcessorFactories,
        root, criteriaQuery, entityManager.getCriteriaBuilder());
  }

  private void test(String expectedSql, FilterNode filterNode) {

    criteriaQuery.select(root).where((Expression<Boolean>) transformer.transform(filterNode));
    TypedQuery<TestEntity> query = entityManager.createQuery(criteriaQuery);
    List<TestEntity> transformerResults = query.getResultList();

    List<TestEntity> expectedResults = entityManager.createQuery(expectedSql, TestEntity.class)
        .getResultList();

    Assertions.assertFalse(expectedResults.isEmpty(), "Queries should return at least one result");

    Assertions.assertEquals(expectedResults.size(), transformerResults.size());

    for (int i = 0; i < expectedResults.size(); i++) {
      Assertions.assertEquals(expectedResults.get(i).getId(),
          transformerResults.get(i).getId());
    }

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
        fb.field("string").equal(fb.input("hello world")).get());

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
        fb.field("string").notEqual(fb.input("hello world")).get());

  }

}
