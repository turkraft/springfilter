package com.turkraft.springfilter;

import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class EntityFilterArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {

    return methodParameter.getParameterType().equals(Specification.class)
        && methodParameter.hasParameterAnnotation(EntityFilter.class);

  }

  @Override
  public Object resolveArgument(
      MethodParameter methodParameter,
      ModelAndViewContainer modelAndViewContainer,
      NativeWebRequest nativeWebRequest,
      WebDataBinderFactory webDataBinderFactory) throws Exception {

    EntityFilter entityFilter = methodParameter.getParameterAnnotation(EntityFilter.class);

    return getSpecification(methodParameter.getGenericParameterType().getClass(),
        !entityFilter.filterParameterName().isEmpty()
            ? nativeWebRequest.getParameterValues(entityFilter.filterParameterName())
            : null,
        !entityFilter.sortParameterName().isEmpty()
            ? nativeWebRequest.getParameterValues(entityFilter.sortParameterName())
            : null);

  }

  private <T> Specification<?> getSpecification(
      Class<?> specificationClass,
      String[] filterInputs,
      String[] orderInputs) {

    Specification<T> result = null;

    if (filterInputs != null && filterInputs.length > 0) {

      for (String input : filterInputs) {
        if (input.trim().length() > 0) {
          result = new FilterSpecification<T>(input).and(result);
        }
      }

    }

    if (orderInputs != null && orderInputs.length > 0) {
      result = new FilterSpecification<T>("", String.join(",", orderInputs)).and(result);
    }

    return result;

  }

}
