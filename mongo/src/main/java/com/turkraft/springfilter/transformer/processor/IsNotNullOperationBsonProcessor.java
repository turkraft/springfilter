package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.IsNotNullOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.util.Arrays;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class IsNotNullOperationBsonProcessor implements
    FilterPostfixOperationProcessor<FilterBsonTransformer, Object> {

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<IsNotNullOperator> getDefinitionType() {
    return IsNotNullOperator.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer,
      PostfixOperationNode postfixOperationNode) {
    transformer.registerTargetType(postfixOperationNode, Boolean.class);
    return new Document("$gt",
        Arrays.asList(transformer.transform(postfixOperationNode.getLeft()), null));
  }

}
