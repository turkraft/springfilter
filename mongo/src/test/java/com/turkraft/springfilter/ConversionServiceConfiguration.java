package com.turkraft.springfilter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.turkraft.springfilter.converter.StringCustomObjectIdConverter;
import com.turkraft.springfilter.converter.StringCustomUUIDConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class ConversionServiceConfiguration {

  @Bean
  public ConversionService conversionService() {
    DefaultConversionService conversionService = new DefaultConversionService();
    conversionService.addConverter(new StringCustomObjectIdConverter());
    conversionService.addConverter(new StringCustomUUIDConverter());
    // ISO temporal parsing, standing in for the formatters a web application registers.
    conversionService.addConverter(String.class, Instant.class, Instant::parse);
    conversionService.addConverter(String.class, LocalDate.class, LocalDate::parse);
    conversionService.addConverter(String.class, LocalDateTime.class, LocalDateTime::parse);
    return conversionService;
  }

  @Bean
  public ConverterRegistry converterRegistry() {
    return (ConverterRegistry) conversionService();
  }

}
