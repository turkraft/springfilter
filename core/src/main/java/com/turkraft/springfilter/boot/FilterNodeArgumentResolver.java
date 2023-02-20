package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

class FilterNodeArgumentResolver implements HandlerMethodArgumentResolver {

  private final FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper;

  FilterNodeArgumentResolver(
      FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper) {
    this.filterNodeArgumentResolverHelper = filterNodeArgumentResolverHelper;
  }

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterType().equals(FilterNode.class)
        || isOptionalParameter(methodParameter);
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter,
      ModelAndViewContainer modelAndViewContainer,
      NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {

    Optional<FilterNode> result = filterNodeArgumentResolverHelper.resolve(methodParameter,
        nativeWebRequest,
        false);

    if (isOptionalParameter(methodParameter)) {
      return result;
    } else {
      if (result.isEmpty()) {
        return null;
      } else {
        return result.get();
      }
    }

  }

  private boolean isOptionalParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterType().equals(
        Optional.class) && methodParameter.getGenericParameterType().getTypeName()
        .equals(Optional.class.getName() + "<" + FilterNode.class.getName() + ">");
  }

}
