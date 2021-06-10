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

    Filter filter = parameter.getParameterAnnotation(Filter.class);

    if (filter.entityClass().equals(Void.class)) {
      throw new IllegalArgumentException(
          "The entity class should be provided when using @Filter with MongoDB, for example: @Filter(entityClass = Car.class)");
    }

    Bson bson = getBson(filter.entityClass(),
        !filter.parameterName().isEmpty() ? webRequest.getParameterValues(filter.parameterName())
            : null);

    if (parameter.getParameterType().equals(Bson.class)) {
      return bson;
    }

    return BsonGeneratorUtils.getDocumentFromBson(bson);
  }

  private Bson getBson(Class<?> entityClass, String[] inputs) {

    IExpression filter = SpringFilterUtils.getFilterFromInputs(inputs);

    return filter == null ? null : BsonGenerator.run(entityClass, filter);

  }

}
