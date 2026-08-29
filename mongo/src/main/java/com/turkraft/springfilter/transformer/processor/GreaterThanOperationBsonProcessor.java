package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.BsonHelper;
import com.turkraft.springfilter.language.GreaterThanOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public class GreaterThanOperationBsonProcessor extends InfixOperationBsonProcessor {

  public GreaterThanOperationBsonProcessor(BsonHelper bsonHelper) {
    super(bsonHelper);
  }

  @Override
  public Class<GreaterThanOperator> getDefinitionType() {
    return GreaterThanOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$gt";
  }

  @Override
  public Object process(FilterBsonTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return super.process(transformer, source);
  }

}
