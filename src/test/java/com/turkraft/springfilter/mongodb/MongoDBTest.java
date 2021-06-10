package com.turkraft.springfilter.mongodb;

import static com.turkraft.springfilter.FilterBuilder.and;
import static com.turkraft.springfilter.FilterBuilder.equal;
import static com.turkraft.springfilter.FilterBuilder.greaterThan;
import static com.turkraft.springfilter.FilterBuilder.greaterThanOrEqual;
import static com.turkraft.springfilter.FilterBuilder.in;
import static com.turkraft.springfilter.FilterBuilder.lessThan;
import static com.turkraft.springfilter.FilterBuilder.lessThanOrEqual;
import static com.turkraft.springfilter.FilterBuilder.not;
import static com.turkraft.springfilter.FilterBuilder.or;
import static com.turkraft.springfilter.FilterBuilder.strings;
import static org.junit.Assert.assertEquals;
import java.time.Instant;
import java.util.Date;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import com.mongodb.client.model.Filters;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.generator.BsonGenerator;

@TestInstance(Lifecycle.PER_CLASS)
class MongoDBTest {

  void queryTest(IExpression expression, Bson query) {
    assertEquals(query, BsonGenerator.run(Car.class, expression));
  }

  @Test
  void equalStringTest() throws Exception {
    queryTest(equal("key", "value"), Filters.eq("key", "value"));
  }

  @Test
  void equalNumberTest() throws Exception {
    queryTest(equal("key", 0), Filters.eq("key", 0));
  }

  @Test
  void greaterThanTest() throws Exception {
    queryTest(greaterThan("key", 0), Filters.gt("key", 0));
  }

  @Test
  void greaterThanOrEqualTest() throws Exception {
    queryTest(greaterThanOrEqual("key", 0), Filters.gte("key", 0));
  }

  @Test
  void lessThanTest() throws Exception {
    queryTest(lessThan("key", 0), Filters.lt("key", 0));
  }

  @Test
  void lessThanOrEqualTest() throws Exception {
    queryTest(lessThanOrEqual("key", 0), Filters.lte("key", 0));
  }

  @Test
  void andTest() throws Exception {
    queryTest(and(equal("a", "b"), equal("c", "d")),
        Filters.and(Filters.eq("a", "b"), Filters.eq("c", "d")));
  }

  @Test
  void orTest() throws Exception {
    queryTest(or(equal("a", "b"), equal("c", "d")),
        Filters.or(Filters.eq("a", "b"), Filters.eq("c", "d")));
  }

  @Test
  void notTest() throws Exception {
    queryTest(not(equal("key", "value")), Filters.not(Filters.eq("key", "value")));
  }

  @Test
  void inTest() throws Exception {
    queryTest(in("a", strings("a", "b", "c")), Filters.in("a", "a", "b", "c"));
  }

  @Test
  void inputConversionTest() throws Exception {
    Date date = Date.from(Instant.now());
    queryTest(equal("key", date), Filters.eq("key", date));
  }

}
