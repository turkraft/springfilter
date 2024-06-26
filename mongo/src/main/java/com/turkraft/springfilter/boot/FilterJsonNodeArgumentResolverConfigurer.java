package com.turkraft.springfilter.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.helper.JsonNodeHelper;
import com.turkraft.springfilter.transformer.TransformerUtils;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class FilterJsonNodeArgumentResolverConfigurer implements WebMvcConfigurer {

  protected final ConversionService conversionService;

  protected final ObjectMapper objectMapper;

  protected final FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper;

  protected final JsonNodeHelper jsonNodeHelper;

  protected final FilterNodeProcessorFactories filterNodeProcessorFactories;

  protected final FieldTypeResolver fieldTypeResolver;

  protected final TransformerUtils transformerUtils;

  public FilterJsonNodeArgumentResolverConfigurer(
      @Lazy @Qualifier("sfConversionService") ConversionService conversionService,
      @Lazy ObjectMapper objectMapper,
      @Lazy FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper,
      @Lazy JsonNodeHelper jsonNodeHelper,
      @Lazy FilterNodeProcessorFactories filterNodeProcessorFactories,
      FieldTypeResolver fieldTypeResolver,
      TransformerUtils transformerUtils) {
    this.conversionService = conversionService;
    this.objectMapper = objectMapper;
    this.filterNodeArgumentResolverHelper = filterNodeArgumentResolverHelper;
    this.jsonNodeHelper = jsonNodeHelper;
    this.filterNodeProcessorFactories = filterNodeProcessorFactories;
    this.fieldTypeResolver = fieldTypeResolver;
    this.transformerUtils = transformerUtils;
  }

  @Override
  public void addArgumentResolvers(
      List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new FilterJsonNodeArgumentResolver(conversionService, objectMapper,
        filterNodeArgumentResolverHelper, jsonNodeHelper,
        filterNodeProcessorFactories, fieldTypeResolver, transformerUtils));
  }

}
