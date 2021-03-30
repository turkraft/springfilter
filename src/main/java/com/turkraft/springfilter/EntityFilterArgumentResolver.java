package com.turkraft.springfilter;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.turkraft.springfilter.node.IExpression;

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
        !entityFilter.parameterName().isEmpty()
            ? nativeWebRequest.getParameterValues(entityFilter.parameterName())
            : null);

  }

  private <T> Specification<?> getSpecification(Class<?> specificationClass, String[] inputs) {

    Specification<T> result = null;

    if (inputs != null && inputs.length > 0) {

      Collection<IExpression> filters = new ArrayList<IExpression>();

      for (String input : inputs) {
        if (input.trim().length() > 0) {
          filters.add(FilterParser.parse(input.trim()));
        }
      }

      return new FilterSpecification<T>(FilterQueryBuilder.and(filters));

    }

    return result;

  }

}
