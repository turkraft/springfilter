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
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.PropertyFilter;

@AutoConfiguration
public class PageSortAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public SortParser sortParser() {
    return new SimpleSortParser();
  }

  @Bean
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  @ConditionalOnClass(name = "jakarta.servlet.http.HttpServletRequest")
  public FieldsFilterCleanupFilter fieldsFilterCleanupFilter() {
    return new FieldsFilterCleanupFilter();
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
        builder.addMixIn(Object.class, AntPathFilterMixin.class);
        builder.filterProvider(new DynamicFilterProvider());
      };
    }
  }

  static class DynamicFilterProvider extends FilterProvider {

    @Override
    public FilterProvider snapshot() {
      return this;
    }

    @Override
    public PropertyFilter findPropertyFilter(SerializationContext ctxt,
        Object filterId, Object value) {
      FieldsExpression fields = FieldsFilterContext.get();
      if (fields != null && AntPathFilterMixin.FILTER.equals(filterId)) {
        return new AntPathPropertyFilter(fields);
      }
      return null;
    }

  }

}
