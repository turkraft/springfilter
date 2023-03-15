package com.turkraft.springfilter.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.lang.Nullable;

@Configuration
public class FilterConversionServiceConfiguration {

  @Nullable
  protected final ConversionService mvcConversionService;

  @Nullable
  protected final ConversionService defaultConversionService;

  public FilterConversionServiceConfiguration(
      @Nullable @Autowired(required = false) ConversionService mvcConversionService,
      @Nullable @Autowired(required = false) ConversionService defaultConversionService) {
    this.mvcConversionService = mvcConversionService;
    this.defaultConversionService = defaultConversionService;
  }

  @Bean
  public ConversionService sfConversionService() {
    if (defaultConversionService != null) {
      return defaultConversionService;
    }
    if (mvcConversionService != null) {
      return mvcConversionService;
    }
    throw new IllegalArgumentException("Could not find any ConversionService bean!");
  }

  @Bean
  public ConverterRegistry sfConverterRegistry() {
    ConversionService conversionService = sfConversionService();
    if (conversionService instanceof ConverterRegistry) {
      return (ConverterRegistry) sfConversionService();
    }
    throw new IllegalArgumentException("Could not find any ConverterRegistry bean!");
  }

}
