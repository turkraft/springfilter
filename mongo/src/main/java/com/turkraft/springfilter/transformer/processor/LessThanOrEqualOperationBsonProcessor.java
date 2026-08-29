package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.BsonHelper;
import com.turkraft.springfilter.language.LessThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public class LessThanOrEqualOperationBsonProcessor extends InfixOperationBsonProcessor {

  public LessThanOrEqualOperationBsonProcessor(BsonHelper bsonHelper) {
    super(bsonHelper);
  }

  @Override
  public Class<LessThanOrEqualOperator> getDefinitionType() {
    return LessThanOrEqualOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$lte";
  }

  @Override
  public Object process(FilterBsonTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return super.process(transformer, source);
  }

}
