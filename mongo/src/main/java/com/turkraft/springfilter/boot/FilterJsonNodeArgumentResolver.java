package com.turkraft.springfilter.boot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.helper.JsonNodeHelper;
import com.turkraft.springfilter.transformer.TransformerUtils;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import java.util.Optional;
import org.bson.Document;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FilterJsonNodeArgumentResolver implements HandlerMethodArgumentResolver {

  protected final ConversionService conversionService;

  protected final ObjectMapper objectMapper;

  protected final FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper;

  protected final JsonNodeHelper jsonNodeHelper;

  protected final FilterNodeProcessorFactories filterNodeProcessorFactories;

  protected final FieldTypeResolver fieldTypeResolver;

  protected final TransformerUtils transformerUtils;

  public FilterJsonNodeArgumentResolver(
          ConversionService conversionService, ObjectMapper objectMapper,
          FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper,
          JsonNodeHelper jsonNodeHelper,
          FilterNodeProcessorFactories filterNodeProcessorFactories,
          FieldTypeResolver fieldTypeResolver, TransformerUtils transformerUtils) {
    this.conversionService = conversionService;
    this.objectMapper = objectMapper;
    this.filterNodeArgumentResolverHelper = filterNodeArgumentResolverHelper;
    this.jsonNodeHelper = jsonNodeHelper;
    this.filterNodeProcessorFactories = filterNodeProcessorFactories;
    this.fieldTypeResolver = fieldTypeResolver;
      this.transformerUtils = transformerUtils;
  }

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.hasParameterAnnotation(Filter.class)
        && (methodParameter.getParameterType().isAssignableFrom(ObjectNode.class)
        || methodParameter.getParameterType().isAssignableFrom(Document.class)
        || methodParameter.getParameterType().isAssignableFrom(Query.class)
        || isOptionalParameter(
        methodParameter, ObjectNode.class) || isOptionalParameter(methodParameter, Document.class)
        || isOptionalParameter(methodParameter, Query.class));
  }

  private boolean isOptionalParameter(MethodParameter methodParameter,
      Class<?> klass) {
    if (!methodParameter.getParameterType().equals(
        Optional.class)) {
      return false;
    }
    try {
      Class<?> optionalClass = Class.forName(methodParameter.getGenericParameterType().getTypeName()
          .substring(methodParameter.getGenericParameterType().getTypeName().indexOf('<') + 1,
              methodParameter.getGenericParameterType().getTypeName().lastIndexOf('>')));
      return optionalClass.isAssignableFrom(klass);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
          "Could not find class " + methodParameter.getParameterType().getTypeName());
    }
  }

  @NonNull
  @Override
  public Object resolveArgument(MethodParameter methodParameter,
      ModelAndViewContainer modelAndViewContainer,
      NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {

    Optional<FilterNode> result = filterNodeArgumentResolverHelper.resolve(methodParameter,
        nativeWebRequest,
        true);

    if (result.isEmpty()) {
      if (methodParameter.getParameterType().equals(Optional.class)) {
        return Optional.empty();
      }
      if (methodParameter.getParameterType().isAssignableFrom(ObjectNode.class)) {
        return objectMapper.createObjectNode();
      } else if (methodParameter.getParameterType().isAssignableFrom(Document.class)) {
        return Document.parse("{}");
      } else if (methodParameter.getParameterType().isAssignableFrom(Query.class)) {
        return new BasicQuery((String) null);
      }
    }

    FilterJsonNodeTransformer filterJsonNodeTransformer = new FilterJsonNodeTransformer(
        conversionService, objectMapper, filterNodeProcessorFactories, fieldTypeResolver,
        methodParameter.getParameterAnnotation(Filter.class).entityClass());

    JsonNode transform = filterJsonNodeTransformer.transform(result.get());
    transform = transformerUtils.simplify(filterJsonNodeTransformer,transform);


    ObjectNode jsonResult = jsonNodeHelper.wrapWithMongoExpression(
            transform);

    if (methodParameter.getParameterType().isAssignableFrom(ObjectNode.class)) {
      return jsonResult;
    } else if (isOptionalParameter(methodParameter, ObjectNode.class)) {
      return Optional.of(jsonResult);
    } else if (methodParameter.getParameterType().isAssignableFrom(Document.class)) {
      return Document.parse(jsonResult.toString());
    } else if (isOptionalParameter(methodParameter, Document.class)) {
      return Optional.of(Document.parse(jsonResult.toString()));
    } else if (methodParameter.getParameterType().isAssignableFrom(Query.class)) {
      return new BasicQuery(Document.parse(jsonResult.toString()));
    } else if (isOptionalParameter(methodParameter, Query.class)) {
      return Optional.of(new BasicQuery(Document.parse(jsonResult.toString())));
    }

    throw new IllegalStateException(
        "Unsupported method parameter " + methodParameter.getParameterType());

  }

}
