package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.converter.FilterQueryConverter;
import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.Optional;
import org.bson.Document;
import org.springframework.core.MethodParameter;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FilterBsonArgumentResolver implements HandlerMethodArgumentResolver {

  protected final FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper;

  protected final FilterQueryConverter filterQueryConverter;

  public FilterBsonArgumentResolver(
      FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper,
      FilterQueryConverter filterQueryConverter) {
    this.filterNodeArgumentResolverHelper = filterNodeArgumentResolverHelper;
    this.filterQueryConverter = filterQueryConverter;
  }

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.hasParameterAnnotation(Filter.class)
        && (methodParameter
        .getParameterType()
        .isAssignableFrom(Document.class)
        || methodParameter
        .getParameterType()
        .isAssignableFrom(Query.class)
        || isOptionalParameter(methodParameter, Document.class)
        || isOptionalParameter(methodParameter, Query.class));
  }

  private boolean isOptionalParameter(MethodParameter methodParameter,
      Class<?> klass) {
    if (!methodParameter
        .getParameterType()
        .equals(
            Optional.class)) {
      return false;
    }
    try {
      Class<?> optionalClass = Class.forName(methodParameter
          .getGenericParameterType()
          .getTypeName()
          .substring(methodParameter
                  .getGenericParameterType()
                  .getTypeName()
                  .indexOf('<') + 1,
              methodParameter
                  .getGenericParameterType()
                  .getTypeName()
                  .lastIndexOf('>')));
      return optionalClass.isAssignableFrom(klass);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
          "Could not find class " + methodParameter
              .getParameterType()
              .getTypeName());
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
      if (methodParameter
          .getParameterType()
          .equals(Optional.class)) {
        return Optional.empty();
      }
      if (methodParameter
          .getParameterType()
          .isAssignableFrom(Document.class)) {
        return new Document();
      }
      return new BasicQuery(new Document());
    }
    Class<?> entityClass = methodParameter
        .getParameterAnnotation(Filter.class)
        .entityClass();
    if (methodParameter
        .getParameterType()
        .isAssignableFrom(Document.class)) {
      return filterQueryConverter.convertToDocument(result.get(), entityClass);
    } else if (isOptionalParameter(methodParameter, Document.class)) {
      return Optional.of(filterQueryConverter.convertToDocument(result.get(), entityClass));
    } else if (methodParameter
        .getParameterType()
        .isAssignableFrom(Query.class)) {
      return filterQueryConverter.convert(result.get(), entityClass);
    } else if (isOptionalParameter(methodParameter, Query.class)) {
      return Optional.of(filterQueryConverter.convert(result.get(), entityClass));
    }
    throw new IllegalStateException(
        "Unsupported method parameter " + methodParameter.getParameterType());
  }

}
