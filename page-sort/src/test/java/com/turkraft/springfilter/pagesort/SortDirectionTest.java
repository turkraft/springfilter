package com.turkraft.springfilter.pagesort;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class SortDirectionTest {

  @Test
  void testParseAsc() {
    assertEquals(SortDirection.ASC, SortDirection.parse("asc"));
    assertEquals(SortDirection.ASC, SortDirection.parse("ASC"));
    assertEquals(SortDirection.ASC, SortDirection.parse("Asc"));
    assertEquals(SortDirection.ASC, SortDirection.parse("ascending"));
    assertEquals(SortDirection.ASC, SortDirection.parse("ASCENDING"));
  }

  @Test
  void testParseDesc() {
    assertEquals(SortDirection.DESC, SortDirection.parse("desc"));
    assertEquals(SortDirection.DESC, SortDirection.parse("DESC"));
    assertEquals(SortDirection.DESC, SortDirection.parse("Desc"));
    assertEquals(SortDirection.DESC, SortDirection.parse("descending"));
    assertEquals(SortDirection.DESC, SortDirection.parse("DESCENDING"));
  }

  @Test
  void testParseNullDefaultsToAsc() {
    assertEquals(SortDirection.ASC, SortDirection.parse(null));
  }

  @Test
  void testParseEmptyDefaultsToAsc() {
    assertEquals(SortDirection.ASC, SortDirection.parse(""));
    assertEquals(SortDirection.ASC, SortDirection.parse("   "));
  }

  @Test
  void testParseInvalidThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> SortDirection.parse("invalid"));
    assertThrows(IllegalArgumentException.class, () -> SortDirection.parse("up"));
    assertThrows(IllegalArgumentException.class, () -> SortDirection.parse("down"));
  }

  @Test
  void testToSpringDirection() {
    assertEquals(Sort.Direction.ASC, SortDirection.ASC.toSpringDirection());
    assertEquals(Sort.Direction.DESC, SortDirection.DESC.toSpringDirection());
  }

  @Test
  void testGetters() {
    assertEquals("asc", SortDirection.ASC.getShortName());
    assertEquals("ascending", SortDirection.ASC.getLongName());
    assertEquals("desc", SortDirection.DESC.getShortName());
    assertEquals("descending", SortDirection.DESC.getLongName());
  }

}
