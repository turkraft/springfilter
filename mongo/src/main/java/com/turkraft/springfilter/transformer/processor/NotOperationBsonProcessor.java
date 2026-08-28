package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.NotOperator;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class NotOperationBsonProcessor implements
    FilterPrefixOperationProcessor<FilterBsonTransformer, Object> {

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<NotOperator> getDefinitionType() {
    return NotOperator.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer,
      PrefixOperationNode prefixOperationNode) {
    transformer.registerTargetType(prefixOperationNode, Boolean.class);
    transformer.registerTargetType(prefixOperationNode.getRight(), Boolean.class);
    return new Document("$not", transformer.transform(prefixOperationNode.getRight()));
  }

}
