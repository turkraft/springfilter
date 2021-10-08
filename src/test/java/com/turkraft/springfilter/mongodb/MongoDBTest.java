package com.turkraft.springfilter.mongodb;

import static com.turkraft.springfilter.FilterBuilder.and;
import static com.turkraft.springfilter.FilterBuilder.equal;
import static com.turkraft.springfilter.FilterBuilder.greaterThan;
import static com.turkraft.springfilter.FilterBuilder.greaterThanOrEqual;
import static com.turkraft.springfilter.FilterBuilder.in;
import static com.turkraft.springfilter.FilterBuilder.inputs;
import static com.turkraft.springfilter.FilterBuilder.lessThan;
import static com.turkraft.springfilter.FilterBuilder.lessThanOrEqual;
import static com.turkraft.springfilter.FilterBuilder.not;
import static com.turkraft.springfilter.FilterBuilder.or;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import com.mongodb.client.model.Filters;
import com.turkraft.springfilter.parser.Filter;
import com.turkraft.springfilter.parser.generator.bson.BsonGenerator;

@TestInstance(Lifecycle.PER_CLASS)
class MongoDBTest {

  void queryTest(Filter expression, Bson query) {
    assertEquals(query, BsonGenerator.run(expression, Car.class));
  }

  @Test
  void equalStringTest() throws Exception {
    queryTest(equal("name", "value"), Filters.eq("name", "value"));
  }

  @Test
  void equalNumberTest() throws Exception {
    queryTest(equal("someNumber", 0), Filters.eq("someNumber", 0));
  }

  @Test
  void greaterThanTest() throws Exception {
    queryTest(greaterThan("someNumber", 0), Filters.gt("someNumber", 0));
  }

  @Test
  void greaterThanOrEqualTest() throws Exception {
    queryTest(greaterThanOrEqual("someNumber", 0), Filters.gte("someNumber", 0));
  }

  @Test
  void lessThanTest() throws Exception {
    queryTest(lessThan("someNumber", 0), Filters.lt("someNumber", 0));
  }

  @Test
  void lessThanOrEqualTest() throws Exception {
    queryTest(lessThanOrEqual("someNumber", 0), Filters.lte("someNumber", 0));
  }

  @Test
  void andTest() throws Exception {
    queryTest(and(equal("name", "x"), equal("id", "y")),
        Filters.and(Filters.eq("name", "x"), Filters.eq("id", "y")));
  }

  @Test
  void orTest() throws Exception {
    queryTest(or(equal("name", "x"), equal("id", "y")),
        Filters.or(Filters.eq("name", "x"), Filters.eq("id", "y")));
  }

  @Test
  void notTest() throws Exception {
    queryTest(not(equal("name", "value")), Filters.not(Filters.eq("name", "value")));
  }

  @Test
  void inTest() throws Exception {
    queryTest(in("id", inputs("x", "y", "z")), Filters.in("id", "x", "y", "z"));
    queryTest(in("someNumber", inputs(1, 2, 3)), Filters.in("someNumber", 1, 2, 3));
  }

}
