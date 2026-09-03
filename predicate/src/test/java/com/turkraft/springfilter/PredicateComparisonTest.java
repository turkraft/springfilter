package com.turkraft.springfilter;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.language.HelloWorldPlaceholder;
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
public class PredicateComparisonTest {

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
  private HelloWorldPlaceholder helloWorldPlaceholder;

  private FilterPredicateTransformer transformer;

  private final TestPojo pojo = new TestPojo("Johnny", List.of(), 30, Map.of(),
      new TestPojo.NestedPojo("name", 1));

  @BeforeEach
  void init() {
    transformer = new FilterPredicateTransformer(conversionService,
        filterNodeProcessorFactories, TestPojo.class);
  }

  private boolean matches(FilterNode filter, TestPojo target) {
    return transformer
        .transform(filter)
        .test(target);
  }

  @Test
  void orderingANumericFieldAgainstUnconvertibleTextReportsBothTypes() {

    List<FilterNode> filters = List.of(
        fb
            .field("integer")
            .greaterThan(fb.input("abc"))
            .get(),
        fb
            .field("integer")
            .greaterThanOrEqual(fb.input("abc"))
            .get(),
        fb
            .field("integer")
            .lessThan(fb.input("abc"))
            .get(),
        fb
            .field("integer")
            .lessThanOrEqual(fb.input("abc"))
            .get());

    for (FilterNode filter : filters) {

      Predicate<Object> predicate = transformer.transform(filter);

      IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
          () -> predicate.test(pojo));

      Assertions.assertTrue(exception
          .getMessage()
          .contains("java.lang.Integer"));

      Assertions.assertTrue(exception
          .getMessage()
          .contains("java.lang.String"));

    }

  }

  @Test
  void orderingComparableValuesOfTheSameTypeStillWorks() {

    Assertions.assertTrue(matches(fb
        .field("integer")
        .greaterThan(fb.input(10))
        .get(), pojo));

    Assertions.assertFalse(matches(fb
        .field("integer")
        .greaterThan(fb.input(30))
        .get(), pojo));

    Assertions.assertFalse(matches(fb
        .field("integer")
        .greaterThan(fb.input(100))
        .get(), pojo));

    Assertions.assertTrue(matches(fb
        .field("integer")
        .greaterThanOrEqual(fb.input(10))
        .get(), pojo));

    Assertions.assertTrue(matches(fb
        .field("integer")
        .greaterThanOrEqual(fb.input(30))
        .get(), pojo));

    Assertions.assertFalse(matches(fb
        .field("integer")
        .greaterThanOrEqual(fb.input(100))
        .get(), pojo));

    Assertions.assertFalse(matches(fb
        .field("integer")
        .lessThan(fb.input(10))
        .get(), pojo));

    Assertions.assertFalse(matches(fb
        .field("integer")
        .lessThan(fb.input(30))
        .get(), pojo));

    Assertions.assertTrue(matches(fb
        .field("integer")
        .lessThan(fb.input(100))
        .get(), pojo));

    Assertions.assertFalse(matches(fb
        .field("integer")
        .lessThanOrEqual(fb.input(10))
        .get(), pojo));

    Assertions.assertTrue(matches(fb
        .field("integer")
        .lessThanOrEqual(fb.input(30))
        .get(), pojo));

    Assertions.assertTrue(matches(fb
        .field("integer")
        .lessThanOrEqual(fb.input(100))
        .get(), pojo));

  }

  @Test
  void orderingAgainstTextThatConvertsCleanlyStillWorks() {

    Assertions.assertTrue(matches(fb
        .field("integer")
        .greaterThan(fb.input("10"))
        .get(), pojo));

    Assertions.assertTrue(matches(fb
        .field("integer")
        .lessThan(fb.input("100"))
        .get(), pojo));

  }

  @Test
  void orderingAgainstAValueThatIsNotComparableAtAllStillReportsIllegalState() {

    FilterNode filter = fb
        .field("nested")
        .greaterThan(fb.input("anything"))
        .get();

    Predicate<Object> predicate = transformer.transform(filter);

    Assertions.assertThrows(IllegalStateException.class, () -> predicate.test(pojo));

  }

  @Test
  void thePlaceholderRegisteredByCoreIsUsableInThisBackendToo() {

    TestPojo greeting = new TestPojo("Hello world!", List.of(), 1, Map.of(),
        new TestPojo.NestedPojo("name", 1));

    FilterNode filter = fb
        .field("string")
        .equal(fb.placeholder(helloWorldPlaceholder))
        .get();

    Assertions.assertTrue(matches(filter, greeting));
    Assertions.assertFalse(matches(filter, pojo));

  }

  @Test
  void caseInsensitiveLikeMatchesTheCaseSensitiveTranslation() {

    Assertions.assertTrue(matches(fb
        .field("string")
        .like(fb.input("John%"))
        .get(), pojo));

    Assertions.assertTrue(matches(fb
        .field("string")
        .insensitiveLike(fb.input("JOHN%"))
        .get(), pojo));

    Assertions.assertTrue(matches(fb
        .field("string")
        .insensitiveLike(fb.input("jOhN%"))
        .get(), pojo));

    Assertions.assertFalse(matches(fb
        .field("string")
        .insensitiveLike(fb.input("Jane%"))
        .get(), pojo));

    Assertions.assertTrue(matches(fb
        .field("string")
        .insensitiveLike(fb.input("j_hnny"))
        .get(), pojo));

  }

}
