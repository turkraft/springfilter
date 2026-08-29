package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.helper.BsonHelper;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import java.util.Objects;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class FilterQueryConverterImpl implements FilterQueryConverter {

  private final ConversionService conversionService;

  private final FilterNodeProcessorFactories filterNodeProcessorFactories;

  private final FieldTypeResolver fieldTypeResolver;

  private final BsonHelper bsonHelper;

  public FilterQueryConverterImpl(
      @Qualifier("sfConversionService") ConversionService conversionService,
      FilterNodeProcessorFactories filterNodeProcessorFactories,
      FieldTypeResolver fieldTypeResolver,
      BsonHelper bsonHelper) {
    this.conversionService = conversionService;
    this.filterNodeProcessorFactories = filterNodeProcessorFactories;
    this.fieldTypeResolver = fieldTypeResolver;
    this.bsonHelper = bsonHelper;
  }

  @Override
  public Query convert(String filter, Class<?> entityClass) {
    return convert(parse(filter), entityClass);
  }

  @Override
  public Query convert(FilterNode filter, Class<?> entityClass) {
    return new BasicQuery(convertToDocument(filter, entityClass));
  }

  @Override
  public Document convertToDocument(String filter, Class<?> entityClass) {
    return convertToDocument(parse(filter), entityClass);
  }

  @Override
  public Document convertToDocument(FilterNode filter, Class<?> entityClass) {
    FilterBsonTransformer transformer = new FilterBsonTransformer(conversionService,
        filterNodeProcessorFactories, fieldTypeResolver, entityClass);
    transformer.registerTargetType(filter, Boolean.class);
    return bsonHelper.wrapWithMongoExpression(transformer.transform(filter));
  }

  private FilterNode parse(String filter) {
    return Objects.requireNonNull(conversionService.convert(filter, FilterNode.class));
  }

}
