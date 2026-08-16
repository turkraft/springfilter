package com.turkraft.springfilter.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
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

  protected final ApplicationContext applicationContext;

  @Nullable
  private ConversionService fallbackConversionService;

  public FilterConversionServiceConfiguration(
      @Nullable @Autowired(required = false) @Qualifier("mvcConversionService") ConversionService mvcConversionService,
      @Nullable @Autowired(required = false) @Qualifier("defaultConversionService") ConversionService defaultConversionService,
      ApplicationContext applicationContext) {
    this.mvcConversionService = mvcConversionService;
    this.defaultConversionService = defaultConversionService;
    this.applicationContext = applicationContext;
  }

  @Bean
  public ConversionService sfConversionService() {
    if (defaultConversionService != null) {
      return defaultConversionService;
    }
    if (mvcConversionService != null) {
      return mvcConversionService;
    }
    if (applicationContext.containsBean("conversionService")) {
      return applicationContext.getBean("conversionService", ConversionService.class);
    }

    if (fallbackConversionService == null) {
      fallbackConversionService =
          new org.springframework.core.convert.support.DefaultConversionService();
    }
    return fallbackConversionService;
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
