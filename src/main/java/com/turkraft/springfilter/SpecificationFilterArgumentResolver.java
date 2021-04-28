package com.turkraft.springfilter;

import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.turkraft.springfilter.node.IExpression;

public class SpecificationFilterArgumentResolver implements HandlerMethodArgumentResolver {

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
        !entityFilter.parameterName().isEmpty()
            ? nativeWebRequest.getParameterValues(entityFilter.parameterName())
            : null);

  }

  private <T> Specification<?> getSpecification(Class<?> specificationClass, String[] inputs) {

    IExpression filter = FilterUtils.getFilterFromInputs(inputs);

    return filter == null ? null : new FilterSpecification<T>(filter);

  }

}
