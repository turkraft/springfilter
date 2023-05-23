package com.turkraft.springfilter.boot;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

  protected final List<ConversionService> conversionServices;

  public FilterConversionServiceConfiguration(
      @Nullable @Autowired(required = false) @Qualifier("mvcConversionService") ConversionService mvcConversionService,
      @Nullable @Autowired(required = false) @Qualifier("defaultConversionService") ConversionService defaultConversionService,
      @Nullable @Autowired(required = false) List<ConversionService> conversionServices) {
    this.mvcConversionService = mvcConversionService;
    this.defaultConversionService = defaultConversionService;
    this.conversionServices = conversionServices;
  }

  @Bean
  public ConversionService sfConversionService() {
    if (defaultConversionService != null) {
      return defaultConversionService;
    }
    if (mvcConversionService != null) {
      return mvcConversionService;
    }
    if (conversionServices != null && !conversionServices.isEmpty()) {
      return conversionServices.get(0);
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
