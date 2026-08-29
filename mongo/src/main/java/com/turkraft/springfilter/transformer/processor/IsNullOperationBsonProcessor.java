package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.IsNullOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.util.Arrays;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class IsNullOperationBsonProcessor implements
    FilterPostfixOperationProcessor<FilterBsonTransformer, Object> {

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<IsNullOperator> getDefinitionType() {
    return IsNullOperator.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer,
      PostfixOperationNode postfixOperationNode) {
    transformer.registerTargetType(postfixOperationNode, Boolean.class);
    // In BSON ordering only null and missing values sort at or below null.
    return new Document("$lte",
        Arrays.asList(transformer.transform(postfixOperationNode.getLeft()), null));
  }

}
