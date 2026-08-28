package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.BsonHelper;
import com.turkraft.springfilter.language.EqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public class EqualOperationBsonProcessor extends InfixOperationBsonProcessor {

  public EqualOperationBsonProcessor(BsonHelper bsonHelper) {
    super(bsonHelper);
  }

  @Override
  public Class<EqualOperator> getDefinitionType() {
    return EqualOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$eq";
  }

  @Override
  public Object process(FilterBsonTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return super.process(transformer, source);
  }

}
