package com.turkraft.springfilter.boot;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.turkraft.springfilter.SpringFilterUtils;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.generator.BsonGenerator;
import com.turkraft.springfilter.generator.BsonGeneratorUtils;

public class BsonFilterArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {

    return (parameter.getParameterType().equals(Bson.class)
        || parameter.getParameterType().equals(Document.class))
        && parameter.hasParameterAnnotation(Filter.class);

  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    Filter entityFilter = parameter.getParameterAnnotation(Filter.class);

    Bson bson = getBson(!entityFilter.parameterName().isEmpty()
        ? webRequest.getParameterValues(entityFilter.parameterName())
        : null);

    if (parameter.getParameterType().equals(Bson.class)) {
      return bson;
    }

    return BsonGeneratorUtils.getDocumentFromBson(bson);

  }

  private Bson getBson(String[] inputs) {

    IExpression filter = SpringFilterUtils.getFilterFromInputs(inputs);

    return filter == null ? null : (new BsonGenerator()).generate(filter);

  }

}
