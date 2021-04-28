package com.turkraft.springfilter.jpa;

import static com.turkraft.springfilter.FilterQueryBuilder.and;
import static com.turkraft.springfilter.FilterQueryBuilder.equal;
import static com.turkraft.springfilter.FilterQueryBuilder.greaterThan;
import static com.turkraft.springfilter.FilterQueryBuilder.greaterThanOrEqual;
import static com.turkraft.springfilter.FilterQueryBuilder.in;
import static com.turkraft.springfilter.FilterQueryBuilder.lessThan;
import static com.turkraft.springfilter.FilterQueryBuilder.lessThanOrEqual;
import static com.turkraft.springfilter.FilterQueryBuilder.not;
import static com.turkraft.springfilter.FilterQueryBuilder.or;
import static com.turkraft.springfilter.FilterQueryBuilder.strings;
import static org.junit.Assert.assertEquals;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import com.mongodb.client.model.Filters;
import com.turkraft.springfilter.node.IExpression;

@TestInstance(Lifecycle.PER_CLASS)
class MongoDBTest {

  void queryTest(IExpression expression, Bson query) {
    assertEquals(query, expression.generateBson());
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

}
