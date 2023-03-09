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
  private final ConversionService mvcConversionService;

  @Nullable
  private final ConversionService defaultConversionService;

  @Nullable
  private final ConverterRegistry converterRegistry;

  public FilterConversionServiceConfiguration(
      @Nullable @Autowired(required = false) ConversionService mvcConversionService,
      @Nullable @Autowired(required = false) ConversionService defaultConversionService,
      @Nullable @Autowired(required = false) ConverterRegistry converterRegistry) {
    this.mvcConversionService = mvcConversionService;
    this.defaultConversionService = defaultConversionService;
    this.converterRegistry = converterRegistry;
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
    if (converterRegistry != null) {
      return converterRegistry;
    }
    return (ConverterRegistry) sfConversionService();
  }

}
