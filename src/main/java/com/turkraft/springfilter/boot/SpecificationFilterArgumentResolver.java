package com.turkraft.springfilter.boot;

import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.turkraft.springfilter.SpringFilterUtils;
import com.turkraft.springfilter.compiler.node.IExpression;

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

    return getSpecification(methodParameter.getGenericParameterType().getClass(),
        nativeWebRequest.getParameterValues(filter.parameterName()));

  }

  private <T> Specification<?> getSpecification(Class<?> specificationClass, String[] inputs) {

    IExpression filter = SpringFilterUtils.getFilterFromInputs(inputs);

    return filter == null ? null : new FilterSpecification<T>(filter);

  }

}
