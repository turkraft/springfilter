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

}
