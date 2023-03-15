package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.language.IsNotEmptyOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public class IsNotEmptyOperationJsonNodeProcessor implements
    FilterPostfixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {


  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<IsNotEmptyOperator> getDefinitionType() {
    return IsNotEmptyOperator.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer,
      PostfixOperationNode postfixOperationNode) {
    transformer.registerTargetType(postfixOperationNode, Boolean.class);
    return transformer.getObjectMapper().createObjectNode().set("$and",
        transformer.getObjectMapper().createArrayNode()
            .add(transformer.getObjectMapper().createObjectNode()
                .set("$isArray", transformer.transform(postfixOperationNode.getLeft())))
            .add(transformer.getObjectMapper().createObjectNode().set("$gt",
                transformer.getObjectMapper().createArrayNode()
                    .add(transformer.getObjectMapper().createObjectNode()
                        .set("$size", transformer.transform(postfixOperationNode.getLeft())))
                    .add(transformer.getObjectMapper().createObjectNode().numberNode(0)))));
  }

}
