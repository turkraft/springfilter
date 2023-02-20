package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.language.InsensitiveLikeOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
class InsensitiveLikeOperationJsonNodeProcessor implements
    FilterInfixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {

  private final LikeOperationJsonNodeProcessor likeOperationJsonNodeProcessor;

  InsensitiveLikeOperationJsonNodeProcessor(
      LikeOperationJsonNodeProcessor likeOperationJsonNodeProcessor) {
    this.likeOperationJsonNodeProcessor = likeOperationJsonNodeProcessor;
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<InsensitiveLikeOperator> getDefinitionType() {
    return InsensitiveLikeOperator.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer,
      InfixOperationNode infixOperationNode) {
    return likeOperationJsonNodeProcessor.getRegexNode(transformer, infixOperationNode, "i");
  }

}
