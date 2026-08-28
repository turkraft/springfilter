package com.turkraft.springfilter;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterQueryConverter;
import com.turkraft.springfilter.converter.FilterQueryConverterImpl;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class FilterQueryConverterImplTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private FilterQueryConverter converter;

  @Autowired
  private FilterBuilder fb;

  @Test
  void isTheAutoConfiguredImplementation() {
    Assertions.assertInstanceOf(FilterQueryConverterImpl.class, converter);
  }

  @Test
  void stringFilterProducesExprQueryWithTypedDate() {
    Query query = converter.convert("instant > '2023-01-01T00:00:00Z'", TestEntity.class);
    Document expected = new Document("$expr", new Document("$gt",
        Arrays.asList("$instant", Date.from(Instant.parse("2023-01-01T00:00:00Z")))));
    Assertions.assertEquals(expected, query.getQueryObject());
  }

  @Test
  void filterNodeProducesTheSameQueryAsItsStringForm() {
    Query fromNode = converter.convert(
        fb.field("id").equal(fb.input("642ebb0e91ac8f778f5654b7")).get(), TestEntity.class);
    Query fromString = converter.convert("id : '642ebb0e91ac8f778f5654b7'", TestEntity.class);
    Document expected = new Document("$expr", new Document("$eq",
        Arrays.asList("$_id", new ObjectId("642ebb0e91ac8f778f5654b7"))));
    Assertions.assertEquals(expected, fromNode.getQueryObject());
    Assertions.assertEquals(expected, fromString.getQueryObject());
  }

  @Test
  void convertToDocumentReturnsTheQueryDocument() {
    Document document = converter.convertToDocument("integer : 5", TestEntity.class);
    Assertions.assertEquals(
        new Document("$expr", new Document("$eq", Arrays.asList("$integer", 5))), document);
    Assertions.assertEquals(document,
        converter.convertToDocument(fb.field("integer").equal(fb.input(5)).get(),
            TestEntity.class));
  }

  @Test
  void unparsableFilterFails() {
    Assertions.assertThrows(RuntimeException.class,
        () -> converter.convert("integer :", TestEntity.class));
  }

}
