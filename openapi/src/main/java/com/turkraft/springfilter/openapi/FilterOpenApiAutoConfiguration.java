package com.turkraft.springfilter.openapi;

import com.turkraft.springfilter.openapi.documentation.FunctionDocumentationProvider;
import com.turkraft.springfilter.openapi.documentation.OperatorDocumentationProvider;
import com.turkraft.springfilter.openapi.generator.FilterExampleGenerator;
import com.turkraft.springfilter.openapi.generator.FilterSchemaGenerator;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector;
import com.turkraft.springfilter.openapi.springdoc.FilterParameterCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(name = "org.springdoc.core.customizers.OperationCustomizer")
public class FilterOpenApiAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public EntityIntrospector entityIntrospector() {
    return new EntityIntrospector();
  }

  @Bean
  @ConditionalOnMissingBean
  public OperatorDocumentationProvider operatorDocumentationProvider(
      com.turkraft.springfilter.definition.FilterOperators filterOperators) {
    return new OperatorDocumentationProvider(filterOperators);
  }

  @Bean
  @ConditionalOnMissingBean
  public FunctionDocumentationProvider functionDocumentationProvider(
      com.turkraft.springfilter.definition.FilterFunctions filterFunctions) {
    return new FunctionDocumentationProvider(filterFunctions);
  }

  @Bean
  @ConditionalOnMissingBean
  public FilterExampleGenerator filterExampleGenerator(EntityIntrospector entityIntrospector) {
    return new FilterExampleGenerator(entityIntrospector);
  }

  @Bean
  @ConditionalOnMissingBean
  public FilterSchemaGenerator filterSchemaGenerator(EntityIntrospector entityIntrospector,
      OperatorDocumentationProvider operatorDocumentationProvider,
      FunctionDocumentationProvider functionDocumentationProvider) {
    return new FilterSchemaGenerator(entityIntrospector, operatorDocumentationProvider,
        functionDocumentationProvider);
  }

  @Bean
  @ConditionalOnMissingBean
  public FilterParameterCustomizer filterParameterCustomizer(
      FilterSchemaGenerator filterSchemaGenerator,
      FilterExampleGenerator filterExampleGenerator) {
    return new FilterParameterCustomizer(filterSchemaGenerator, filterExampleGenerator);
  }

}
