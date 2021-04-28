package com.turkraft.springfilter;

import static com.turkraft.springfilter.FilterBuilder.equal;
import static com.turkraft.springfilter.FilterBuilder.field;
import static com.turkraft.springfilter.FilterBuilder.input;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class FilterBuilderTest {

  @Test
  void test() {
    assertEquals("name : 'jose'", equal(field("name"), input("jose")).generate());
  }

}
