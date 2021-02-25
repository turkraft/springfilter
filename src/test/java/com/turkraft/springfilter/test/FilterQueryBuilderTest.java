package com.turkraft.springfilter.test;

import static com.turkraft.springfilter.FilterQueryBuilder.equal;
import static com.turkraft.springfilter.FilterQueryBuilder.field;
import static com.turkraft.springfilter.FilterQueryBuilder.input;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class FilterQueryBuilderTest {

  @Test
  void test() {
    assertEquals("name : 'jose'", equal(field("name"), input("jose")).generate());
  }

}
