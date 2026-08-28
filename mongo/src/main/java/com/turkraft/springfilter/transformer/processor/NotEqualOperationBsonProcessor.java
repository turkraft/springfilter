package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.BsonHelper;
import com.turkraft.springfilter.language.NotEqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public class NotEqualOperationBsonProcessor extends InfixOperationBsonProcessor {

  public NotEqualOperationBsonProcessor(BsonHelper bsonHelper) {
    super(bsonHelper);
  }

  @Override
  public Class<NotEqualOperator> getDefinitionType() {
    return NotEqualOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$ne";
  }

  @Override
  public Object process(FilterBsonTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return super.process(transformer, source);
  }

}
