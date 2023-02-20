package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
class LikeOperationExpressionProcessor implements
    FilterInfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<LikeOperator> getDefinitionType() {
    return LikeOperator.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    transformer.registerTargetType(source.getLeft(), String.class);
    transformer.registerTargetType(source.getRight(), String.class);
    return transformer.getCriteriaBuilder()
        .like((Expression<String>) transformer.transform(source.getLeft()),
            (Expression<String>) transformer.transform(source.getRight()));
  }

}
