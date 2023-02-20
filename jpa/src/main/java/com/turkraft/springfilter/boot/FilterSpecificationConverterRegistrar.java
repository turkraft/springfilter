package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.ConverterRegistry;

@Configuration
class FilterSpecificationConverterRegistrar {

  public FilterSpecificationConverterRegistrar(ConverterRegistry converterRegistry,
      FilterSpecificationConverter filterSpecificationConverter) {
    converterRegistry.addConverter(filterSpecificationConverter);
  }

}
