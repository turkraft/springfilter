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
  FilterStringConverter filterStringConverter;

  private void test(String expected, StepWithResult filter) {
    Assertions.assertEquals(expected, filterStringConverter.convert(filter.get()));
  }

  @Test
  void test1() {
    test("`hello` : 'some string'",
        fb.placeholder(helloWorldPlaceholder).equal(fb.input("some string")));
  }

  @Test
  void test2() {
    test("x : '1' and y >: '2'",
        fb.field("x").equal(fb.input(1)).and(fb.field("y").greaterThanOrEqual(fb.input(2))));
  }

}
