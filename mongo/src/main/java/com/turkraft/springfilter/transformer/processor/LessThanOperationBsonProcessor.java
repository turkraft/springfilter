package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.BsonHelper;
import com.turkraft.springfilter.language.LessThanOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public class LessThanOperationBsonProcessor extends InfixOperationBsonProcessor {

  public LessThanOperationBsonProcessor(BsonHelper bsonHelper) {
    super(bsonHelper);
  }

  @Override
  public Class<LessThanOperator> getDefinitionType() {
    return LessThanOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$lt";
  }

  @Override
  public Object process(FilterBsonTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return super.process(transformer, source);
  }

}
