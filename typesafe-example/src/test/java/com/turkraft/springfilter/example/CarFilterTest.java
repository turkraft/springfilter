package com.turkraft.springfilter.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterStringConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CarFilterTest {

  @Autowired
  private FilterBuilder fb;

  @Autowired
  private FilterStringConverter converter;

  @Test
  void generatedClassExists() {
    assertNotNull(CarFilter.where(fb));
  }

  @Test
  void yearBetween() {
    assertEquals("year between '2020' and '2025'",
        converter.convert(CarFilter
            .where(fb)
            .year()
            .between(2020, 2025)
            .build()));
  }

  @Test
  void modelStartsWith() {
    assertEquals("model ~ 'Audi%'",
        converter.convert(CarFilter
            .where(fb)
            .model()
            .startsWith("Audi")
            .build()));
  }

  @Test
  void chainedAnd() {
    assertEquals("year between '2020' and '2025' and model ~ 'Audi%'",
        converter.convert(CarFilter
            .where(fb)
            .year()
            .between(2020, 2025)
            .and()
            .model()
            .startsWith("Audi")
            .build()));
  }

  @Test
  void orPrecedence() {
    assertEquals("year : '2020' or year : '2025' and sold : 'true'",
        converter.convert(CarFilter
            .where(fb)
            .year()
            .equal(2020)
            .or()
            .year()
            .equal(2025)
            .and()
            .sold()
            .isTrue()
            .build()));
  }

}
