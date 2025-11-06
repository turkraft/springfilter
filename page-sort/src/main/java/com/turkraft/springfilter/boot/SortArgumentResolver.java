package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.pagesort.SortExpression;
import com.turkraft.springfilter.pagesort.SortParser;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SortArgumentResolver implements HandlerMethodArgumentResolver {

  protected final SortParser sortParser;

  public SortArgumentResolver(SortParser sortParser) {
    this.sortParser = Objects.requireNonNull(sortParser, "sortParser must not be null");
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(Sort.class)
        && org.springframework.data.domain.Sort.class.isAssignableFrom(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
      ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    Sort sortAnnotation = parameter.getParameterAnnotation(Sort.class);
    if (sortAnnotation == null) {
      return org.springframework.data.domain.Sort.unsorted();
    }

    String parameterName = sortAnnotation.parameter();
    String sortExpression = webRequest.getParameter(parameterName);

    if (sortExpression == null || sortExpression.trim().isEmpty()) {
      if (sortAnnotation.required()) {
        throw new IllegalArgumentException(
            "Required sort parameter '" + parameterName + "' is missing");
      }

      String defaultValue = sortAnnotation.defaultValue();
      if (defaultValue != null && !defaultValue.isEmpty()) {
        sortExpression = defaultValue;
      } else {
        return org.springframework.data.domain.Sort.unsorted();
      }
    }

    SortExpression parsed = sortParser.parse(sortExpression, sortAnnotation.maxFields());

    return parsed.toSpringSort();

  }

}
