package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.converter.FilterPredicateConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.ConverterRegistry;

@Configuration
public class FilterPredicateConverterRegistrar {

  public FilterPredicateConverterRegistrar(
      @Qualifier("sfConverterRegistry") ConverterRegistry converterRegistry,
      FilterPredicateConverter filterPredicateConverter) {
    converterRegistry.addConverter(filterPredicateConverter);
  }

}
