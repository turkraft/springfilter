package com.turkraft.springfilter.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkraft.springfilter.pagesort.AntPathFilterMixin;
import com.turkraft.springfilter.pagesort.SimpleSortParser;
import com.turkraft.springfilter.pagesort.SortParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class PageSortAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public SortParser sortParser() {
    return new SimpleSortParser();
  }

  @Bean
  @ConditionalOnClass(ObjectMapper.class)
  public Jackson2ObjectMapperBuilderCustomizer fieldFilterCustomizer() {
    return builder -> builder.postConfigurer(objectMapper -> {
      objectMapper.setDefaultMergeable(true);
      objectMapper.addMixIn(Object.class, AntPathFilterMixin.class);
      com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider defaultFilterProvider =
          new com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider()
              .addFilter(AntPathFilterMixin.FILTER,
                  com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAll())
              .setFailOnUnknownId(false);
      objectMapper.setFilterProvider(defaultFilterProvider);
    });
  }

}
