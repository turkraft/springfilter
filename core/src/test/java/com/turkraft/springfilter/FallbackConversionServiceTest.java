package com.turkraft.springfilter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;

@SpringBootTest(classes = FallbackConversionServiceTest.Config.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FallbackConversionServiceTest {

  @SpringBootConfiguration
  @EnableAutoConfiguration
  static class Config {

  }

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  @Qualifier("sfConversionService")
  private ConversionService sfConversionService;

  @Test
  void testFallbackIsUsed() {
    Assertions.assertFalse(applicationContext.containsBean("conversionService"));
    Assertions.assertFalse(applicationContext.containsBean("mvcConversionService"));
    Assertions.assertFalse(applicationContext.containsBean("defaultConversionService"));
  }

  @Test
  void testTemporalConversion() {
    Assertions.assertEquals(LocalDate.of(2024, 1, 2),
        sfConversionService.convert("2024-01-02", LocalDate.class));
    Assertions.assertEquals(LocalDateTime.of(2024, 1, 2, 3, 4, 5),
        sfConversionService.convert("2024-01-02T03:04:05", LocalDateTime.class));
    Assertions.assertEquals(Instant.parse("2024-01-02T03:04:05Z"),
        sfConversionService.convert("2024-01-02T03:04:05Z", Instant.class));
  }

  @Test
  void testScalarConversion() {
    Assertions.assertEquals(42, sfConversionService.convert("42", Integer.class));
    Assertions.assertEquals(true, sfConversionService.convert("true", Boolean.class));
  }

  @Test
  void testConverterRegistry() {
    Assertions.assertInstanceOf(ConverterRegistry.class, sfConversionService);
    Assertions.assertNotNull(applicationContext.getBean("sfConverterRegistry"));
  }

}
