package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.BsonHelper;
import com.turkraft.springfilter.language.AndOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public class AndOperationBsonProcessor extends InfixOperationBsonProcessor {

  public AndOperationBsonProcessor(BsonHelper bsonHelper) {
    super(bsonHelper);
  }

  @Override
  public Class<AndOperator> getDefinitionType() {
    return AndOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$and";
  }

  @Override
  public Object process(FilterBsonTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    transformer.registerTargetType(source.getLeft(), Boolean.class);
    transformer.registerTargetType(source.getRight(), Boolean.class);
    return super.process(transformer, source);
  }

}
