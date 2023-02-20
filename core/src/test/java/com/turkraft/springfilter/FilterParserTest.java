package com.turkraft.springfilter;

import com.turkraft.springfilter.parser.FilterParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FilterParserTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  private TestReporter testReporter;

  @BeforeEach
  void init(TestReporter testReporter) {
    this.testReporter = testReporter;
  }

  @Autowired
  private FilterParser filterParser;

  @Autowired
  private ConversionService conversionService;

  @ParameterizedTest(name = "{0}")
  @CsvFileSource(resources = "/deterministicTestInputs.csv")
  void deterministicTest(String input) {

    String firstPass = conversionService.convert(filterParser.parse(input), String.class);

    String secondPass = conversionService.convert(filterParser.parse(firstPass), String.class);

    Assertions.assertEquals(firstPass, secondPass);

    testReporter.publishEntry("result", input + " => " + secondPass);

  }

}
