package com.turkraft.springfilter;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.turkraft.springfilter.node.IExpression;

public class DocumentFilterArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {

    return parameter.getParameterType().equals(Document.class)
        && parameter.hasParameterAnnotation(EntityFilter.class);

  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    EntityFilter entityFilter = parameter.getParameterAnnotation(EntityFilter.class);

    Bson bson = getBson(!entityFilter.parameterName().isEmpty()
        ? webRequest.getParameterValues(entityFilter.parameterName())
        : null);
    return FilterUtils.getDocumentFromBson(bson);

  }

  private Bson getBson(String[] inputs) {

    IExpression filter = FilterUtils.getFilterFromInputs(inputs);

    return filter == null ? null : filter.generateBson();

  }

}
