package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.pagesort.AntPathFilterMixin;
import com.turkraft.springfilter.pagesort.AntPathPropertyFilter;
import com.turkraft.springfilter.pagesort.FieldsExpression;
import com.turkraft.springfilter.pagesort.FieldsFilterContext;
import com.turkraft.springfilter.pagesort.SimpleSortParser;
import com.turkraft.springfilter.pagesort.SortParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.PropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;

@AutoConfiguration
public class PageSortAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public SortParser sortParser() {
    return new SimpleSortParser();
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass(name = {
      "tools.jackson.databind.ObjectMapper",
      "org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer"
  })
  static class JacksonCompatibilityConfiguration {

    @Bean
    org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer fieldFilterCustomizer() {
      return builder -> {
        builder.defaultMergeable(true);
        builder.addMixIn(Object.class, AntPathFilterMixin.class);
        builder.filterProvider(new DynamicFilterProvider().setFailOnUnknownId(false));
      };
    }
  }

  static class DynamicFilterProvider extends SimpleFilterProvider {

    @Override
    public PropertyFilter findPropertyFilter(SerializationContext ctxt,
        Object filterId, Object value) {
      FieldsExpression fields = FieldsFilterContext.get();
      if (fields != null && AntPathFilterMixin.FILTER.equals(filterId)) {
        return new AntPathPropertyFilter(fields);
      }
      return super.findPropertyFilter(ctxt, filterId, value);
    }

  }

}
