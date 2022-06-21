package com.turkraft.springfilter.boot;

import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.turkraft.springfilter.FilterUtils;
import com.turkraft.springfilter.parser.FilterParser.FilterContext;

/**
 * Resolver for {@link com.turkraft.springfilter.boot.FilterSpecification
 * FilterSpecification&lt;T&gt;}, or {@link org.springframework.data.jpa.domain.Specification
 * Specification&lt;T&gt;} annotated with {@link Filter}.
 */

public class SpecificationFilterArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {

    return (methodParameter.getParameterType().equals(Specification.class)
        && methodParameter.hasParameterAnnotation(Filter.class))
        || methodParameter.getParameterType().equals(FilterSpecification.class);

  }

  @Override
  public Object resolveArgument(
      MethodParameter methodParameter,
      ModelAndViewContainer modelAndViewContainer,
      NativeWebRequest nativeWebRequest,
      WebDataBinderFactory webDataBinderFactory) throws Exception {

    Filter filter = methodParameter.getParameterAnnotation(Filter.class);

    if (!filter.entityClass().equals(Void.class)) {
      throw new IllegalArgumentException(
          "The entity class should not be provided, " + filter.entityClass() + " was given");
    }

    return getSpecification(methodParameter.getGenericParameterType().getClass(),
        nativeWebRequest.getParameterValues(filter.parameterName()));

  }

  private <T> Specification<?> getSpecification(Class<?> specificationClass, String[] inputs) {

    FilterContext filter = FilterUtils.getFilterFromInputs(inputs);

    return filter == null ? null : new FilterSpecification<T>(filter);

  }

}
