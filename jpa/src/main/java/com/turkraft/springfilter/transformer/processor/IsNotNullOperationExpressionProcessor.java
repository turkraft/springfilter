package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.IsNotNullOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
class IsNotNullOperationExpressionProcessor implements
    FilterPostfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<IsNotNullOperator> getDefinitionType() {
    return IsNotNullOperator.class;
  }

  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      PostfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return transformer.getCriteriaBuilder().isNotNull(transformer.transform(source.getLeft()));
  }

}
