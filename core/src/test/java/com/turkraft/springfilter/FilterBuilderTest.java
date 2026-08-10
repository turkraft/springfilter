package com.turkraft.springfilter;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.builder.StepWithResult;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.language.HelloWorldPlaceholder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FilterBuilderTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private FilterBuilder fb;

  @Autowired
  private HelloWorldPlaceholder helloWorldPlaceholder;

  @Autowired
  private FilterStringConverter filterStringConverter;

  private void test(String expected, StepWithResult filter) {
    Assertions.assertEquals(expected, filterStringConverter.convert(filter.get()));
  }

  @Test
  void test1() {
    test("`hello` : 'some string'",
        fb
            .placeholder(helloWorldPlaceholder)
            .equal(fb.input("some string")));
  }

  @Test
  void test2() {
    test("x : '1' and y >: '2'",
        fb
            .field("x")
            .equal(fb.input(1))
            .and(fb
                .field("y")
                .greaterThanOrEqual(fb.input(2))));
  }

  @Test
  void testStartsWith() {
    test("name ~ 'test%'",
        fb
            .field("name")
            .startsWith("test"));
  }

  @Test
  void testEndsWith() {
    test("name ~ '%end'",
        fb
            .field("name")
            .endsWith("end"));
  }

  @Test
  void testContains() {
    test("name ~ '%mid%'",
        fb
            .field("name")
            .contains("mid"));
  }

  @Test
  void testInsensitiveStartsWith() {
    test("name ~~ 'TEST%'",
        fb
            .field("name")
            .insensitiveStartsWith("TEST"));
  }

  @Test
  void testInsensitiveEndsWith() {
    test("name ~~ '%ING'",
        fb
            .field("name")
            .insensitiveEndsWith("ING"));
  }

  @Test
  void testInsensitiveContains() {
    test("name ~~ '%dle%'",
        fb
            .field("name")
            .insensitiveContains("dle"));
  }

  @Test
  void testStartsWithChained() {
    test("name ~ 'test%' and status : 'active'",
        fb
            .field("name")
            .startsWith("test")
            .and(fb
                .field("status")
                .equal(fb.input("active"))));
  }

  @Test
  void testContainsWithSpecialChars() {
    test("name ~ '%100%%'",
        fb
            .field("name")
            .contains("100%"));
  }

  @Test
  void testBetweenIntegers() {
    test("age between '18' and '65'",
        fb
            .field("age")
            .between(fb.input(18), fb.input(65)));
  }

  @Test
  void testBetweenDecimals() {
    test("price between '9.99' and '99.99'",
        fb
            .field("price")
            .between(fb.input(9.99), fb.input(99.99)));
  }

  @Test
  void testBetweenStrings() {
    test("name between 'A' and 'M'",
        fb
            .field("name")
            .between(fb.input("A"), fb.input("M")));
  }

  @Test
  void testBetweenNestedField() {
    test("address.zip between '10000' and '99999'",
        fb
            .field("address.zip")
            .between(fb.input(10000), fb.input(99999)));
  }

  @Test
  void testBetweenWithFieldReferences() {
    test("a between b and c",
        fb
            .field("a")
            .between(fb.field("b"), fb.field("c")));
  }

  @Test
  void testBetweenChainedWithAnd() {
    test("age between '18' and '65' and name : 'John'",
        fb
            .field("age")
            .between(fb.input(18), fb.input(65))
            .and(fb
                .field("name")
                .equal(fb.input("John"))));
  }

  @Test
  void testBetweenChainedWithOr() {
    test("age between '18' and '65' or name : 'John'",
        fb
            .field("age")
            .between(fb.input(18), fb.input(65))
            .or(fb
                .field("name")
                .equal(fb.input("John"))));
  }

  @Test
  void testBetweenWithPlaceholder() {
    test("age between `hello` and `hello`",
        fb
            .field("age")
            .between(
                fb.placeholder(helloWorldPlaceholder),
                fb.placeholder(helloWorldPlaceholder)));
  }

  @Test
  void testLikeCollection() {
    test("name ~ ['A%', 'B%', 'C%']",
        fb
            .field("name")
            .likeCollection(fb.input("A%"), fb.input("B%"), fb.input("C%")));
  }

  @Test
  void testLikeCollectionSingle() {
    test("name ~ ['%test%']",
        fb
            .field("name")
            .likeCollection(fb.input("%test%")));
  }

  @Test
  void testLikeCollectionWithFieldRefs() {
    test("a ~ [b, c]",
        fb
            .field("a")
            .likeCollection(fb.field("b"), fb.field("c")));
  }

  @Test
  void testInsensitiveLikeCollection() {
    test("name ~~ ['A%', 'B%']",
        fb
            .field("name")
            .insensitiveLikeCollection(fb.input("A%"), fb.input("B%")));
  }

}
