package com.torshid.springfilter;

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
  public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
      NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

    return getSpecification(methodParameter.getGenericParameterType().getClass(),
        nativeWebRequest.getParameter(methodParameter.getParameterAnnotation(EntityFilter.class).parameterName()));

  }

  private <T> Specification<?> getSpecification(Class<?> specificationClass, String input) {

    if (input == null || input.trim().isEmpty())
      return null;

    return new FilterSpecification<T>(input);

  }

}
