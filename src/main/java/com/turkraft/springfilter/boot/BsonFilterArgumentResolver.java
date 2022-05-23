package com.turkraft.springfilter.boot;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.turkraft.springfilter.FilterUtils;
import com.turkraft.springfilter.parser.FilterParser.FilterContext;
import com.turkraft.springfilter.parser.generator.bson.BsonGenerator;
import com.turkraft.springfilter.parser.generator.bson.BsonGeneratorUtils;
import org.springframework.data.mongodb.CodecRegistryProvider;
/**
 * Resolver for {@link org.bson.conversions.Bson Bson} and {@link org.bson.Document Document}
 * annotated with {@link Filter}.
 */

@Component
public class BsonFilterArgumentResolver implements HandlerMethodArgumentResolver {

  @Autowired(required = false)
  CodecRegistryProvider codecRegistryProvider;

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

    CodecRegistry codecRegistry = codecRegistryProvider != null ? codecRegistryProvider.getCodecRegistry() : null;

    return BsonGeneratorUtils.getDocumentFromBson(bson, codecRegistry);
  }

  private Bson getBson(Class<?> entityClass, String[] inputs) {

    FilterContext filter = FilterUtils.getFilterFromInputs(inputs);

    return filter == null ? null : BsonGenerator.run(filter, entityClass);

  }

}
