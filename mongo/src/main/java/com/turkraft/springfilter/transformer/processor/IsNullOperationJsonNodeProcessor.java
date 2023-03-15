package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.language.IsNullOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public class IsNullOperationJsonNodeProcessor implements
    FilterPostfixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {


  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<IsNullOperator> getDefinitionType() {
    return IsNullOperator.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer,
      PostfixOperationNode postfixOperationNode) {
    transformer.registerTargetType(postfixOperationNode, Boolean.class);
    return transformer.getObjectMapper().createObjectNode().set("$lte",
        transformer.getObjectMapper().createArrayNode()
            .add(transformer.transform(postfixOperationNode.getLeft()))
            .add(transformer.getObjectMapper().nullNode()));
  }

}
