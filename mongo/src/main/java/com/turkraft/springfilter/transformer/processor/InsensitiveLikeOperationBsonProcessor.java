package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.InsensitiveLikeOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public class InsensitiveLikeOperationBsonProcessor implements
    FilterInfixOperationProcessor<FilterBsonTransformer, Object> {

  private final LikeOperationBsonProcessor likeOperationBsonProcessor;

  public InsensitiveLikeOperationBsonProcessor(
      LikeOperationBsonProcessor likeOperationBsonProcessor) {
    this.likeOperationBsonProcessor = likeOperationBsonProcessor;
  }

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<InsensitiveLikeOperator> getDefinitionType() {
    return InsensitiveLikeOperator.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer,
      InfixOperationNode infixOperationNode) {
    return likeOperationBsonProcessor.getRegexNode(transformer, infixOperationNode, "i");
  }

}
