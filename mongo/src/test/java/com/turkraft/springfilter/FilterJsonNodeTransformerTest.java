package com.turkraft.springfilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import java.io.IOException;
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

  private FilterJsonNodeTransformer transformer;

  @BeforeEach
  void init() {
    transformer = new FilterJsonNodeTransformer(conversionService, objectMapper,
        filterNodeProcessorFactories, TestEntity.class);
  }

  private void test(String expectedJson, FilterNode filterNode) {
    try {
      JsonNode expectedOutput = objectMapper.readTree(expectedJson);
      Assertions.assertEquals(expectedOutput.toString(),
          transformer.transform(filterNode).toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
        fb.field("string").equal(fb.input("hello")).get());
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
        fb.field("string").notEqual(fb.input("hello")).get());
  }

}
