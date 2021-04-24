package com.turkraft.springfilter;

import java.util.ArrayList;
import java.util.Collection;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.conversions.Bson;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.mongodb.MongoClientSettings;
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
    return getDocument(bson);

  }

  private Bson getBson(String[] inputs) {

    if (inputs != null && inputs.length > 0) {

      Collection<IExpression> filters = new ArrayList<>();

      for (String input : inputs) {
        if (input.trim().length() > 0) {
          filters.add(FilterParser.parse(input.trim()));
        }
      }

      return FilterQueryBuilder.and(filters).generateBson();

    }

    return null;

  }

  private Document getDocument(Bson bson) {

    if (bson == null) {
      return null;
    }

    BsonDocument bsonDocument =
        bson.toBsonDocument(BsonDocument.class, MongoClientSettings.getDefaultCodecRegistry());
    DocumentCodec codec = new DocumentCodec();
    DecoderContext decoderContext = DecoderContext.builder().build();
    return codec.decode(new BsonDocumentReader(bsonDocument), decoderContext);

  }

}
