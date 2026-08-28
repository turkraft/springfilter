package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.BsonHelper;
import com.turkraft.springfilter.language.GreaterThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public class GreaterThanOrEqualOperationBsonProcessor extends InfixOperationBsonProcessor {

  public GreaterThanOrEqualOperationBsonProcessor(BsonHelper bsonHelper) {
    super(bsonHelper);
  }

  @Override
  public Class<GreaterThanOrEqualOperator> getDefinitionType() {
    return GreaterThanOrEqualOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$gte";
  }

  @Override
  public Object process(FilterBsonTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return super.process(transformer, source);
  }

}
