package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.converter.FilterPredicate;
import com.turkraft.springfilter.converter.FilterPredicateConverter;
import com.turkraft.springfilter.parser.node.FilterNode;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FilterPredicateArgumentResolver implements HandlerMethodArgumentResolver {

  protected final FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper;

  protected final FilterPredicateConverter filterPredicateConverter;

  public FilterPredicateArgumentResolver(
      FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper,
      FilterPredicateConverter filterPredicateConverter) {
    this.filterNodeArgumentResolverHelper = filterNodeArgumentResolverHelper;
    this.filterPredicateConverter = filterPredicateConverter;
  }

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterType().equals(FilterPredicate.class) || (
        methodParameter.hasParameterAnnotation(Filter.class)
            && methodParameter.getParameterType().equals(Predicate.class))
        || isOptionalParameter(methodParameter);
  }

  @NonNull
  @Override
  public Object resolveArgument(MethodParameter methodParameter,
      ModelAndViewContainer modelAndViewContainer,
      NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {

    Optional<FilterNode> result = filterNodeArgumentResolverHelper.resolve(methodParameter,
        nativeWebRequest,
        false);

    Class<?> entityType = extractEntityType(methodParameter);

    if (isOptionalParameter(methodParameter)) {
      if (result.isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(filterPredicateConverter.convert(result.get(), entityType));
      }
    } else {
      if (result.isEmpty()) {
        return new FilterPredicate<>() {
          @Override
          public boolean test(Object entity) {
            return true;
          }

          @Nullable
          @Override
          public FilterNode getFilter() {
            return null;
          }
        };
      } else {
        return filterPredicateConverter.convert(result.get(), entityType);
      }
    }

  }

  private boolean isOptionalParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterType().equals(
        Optional.class) && (methodParameter.getGenericParameterType().getTypeName()
        .equals(Optional.class.getName() + "<" + FilterPredicate.class.getName() + ">") || (
        methodParameter.hasParameterAnnotation(Filter.class)
            && methodParameter.getGenericParameterType().getTypeName()
            .equals(Optional.class.getName() + "<" + Predicate.class.getName())));
  }

  private Class<?> extractEntityType(MethodParameter methodParameter) {
    Type genericType = methodParameter.getGenericParameterType();

    if (genericType instanceof ParameterizedType parameterizedType) {
      Type[] typeArguments = parameterizedType.getActualTypeArguments();
      if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?>) {
        return (Class<?>) typeArguments[0];
      }
    }

    return Object.class;
  }

}
