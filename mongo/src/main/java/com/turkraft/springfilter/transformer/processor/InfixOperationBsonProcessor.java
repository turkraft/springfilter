package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.BsonHelper;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public abstract class InfixOperationBsonProcessor implements
    FilterInfixOperationProcessor<FilterBsonTransformer, Object> {

  private final BsonHelper bsonHelper;

  public InfixOperationBsonProcessor(BsonHelper bsonHelper) {
    this.bsonHelper = bsonHelper;
  }

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  public abstract String getMongoOperator();

  @Override
  public Object process(FilterBsonTransformer transformer, InfixOperationNode source) {
    return bsonHelper.transform(transformer, source, getMongoOperator());
  }

}
