package com.turkraft.springfilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class ConversionServiceConfiguration {

  @Autowired
  private ConfigurableEnvironment environment;

  @Bean
  public ConversionService conversionService() {
    return environment.getConversionService();
  }

  @Bean
  public ConverterRegistry converterRegistry() {
    return (ConverterRegistry) conversionService();
  }

}
