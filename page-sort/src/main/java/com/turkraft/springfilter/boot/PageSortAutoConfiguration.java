package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.pagesort.AntPathFilterMixin;
import com.turkraft.springfilter.pagesort.SimpleSortParser;
import com.turkraft.springfilter.pagesort.SortParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
public class PageSortAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public SortParser sortParser() {
    return new SimpleSortParser();
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass(name = {
      "com.fasterxml.jackson.databind.ObjectMapper",
      "org.springframework.boot.jackson2.autoconfigure.Jackson2ObjectMapperBuilderCustomizer"
  })
  static class Jackson2CompatibilityConfiguration {

    @Bean
    org.springframework.boot.jackson2.autoconfigure.Jackson2ObjectMapperBuilderCustomizer fieldFilterCustomizer() {
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

}
