package com.turkraft.springfilter;

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
    return conversionService;
  }

  @Bean
  public ConverterRegistry converterRegistry() {
    return (ConverterRegistry) conversionService();
  }

}
