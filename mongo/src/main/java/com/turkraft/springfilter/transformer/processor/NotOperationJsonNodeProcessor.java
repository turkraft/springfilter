package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.language.NotOperator;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public class NotOperationJsonNodeProcessor implements
    FilterPrefixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<NotOperator> getDefinitionType() {
    return NotOperator.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer,
      PrefixOperationNode prefixOperationNode) {
    transformer.registerTargetType(prefixOperationNode, Boolean.class);
    transformer.registerTargetType(prefixOperationNode.getRight(), Boolean.class);
    return transformer.getObjectMapper().createObjectNode()
        .set("$not", transformer.transform(prefixOperationNode.getRight()));
  }

}
