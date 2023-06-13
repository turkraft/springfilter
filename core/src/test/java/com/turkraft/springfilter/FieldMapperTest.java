package com.turkraft.springfilter;

import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.ParseContextImpl;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FieldMapperTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private FilterParser filterParser;

  @Autowired
  private FilterStringConverter filterStringConverter;

  @Test
  void test() {

    FilterNode node = filterParser.parse("x : y : z", new ParseContextImpl(
        (String field) -> field.equalsIgnoreCase("x") ? "a"
            : field.equalsIgnoreCase("y") ? "b" : field));

    Assertions.assertEquals("a : b : z", filterStringConverter.convert(node));

  }

}
