package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.converter.FilterStringConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.ConverterRegistry;

@Configuration
public class FilterStringConverterRegistrar {

  public FilterStringConverterRegistrar(
      @Qualifier("sfConversionService") ConverterRegistry converterRegistry,
      FilterStringConverter filterStringConverter) {
    converterRegistry.addConverter(filterStringConverter);
  }

}
