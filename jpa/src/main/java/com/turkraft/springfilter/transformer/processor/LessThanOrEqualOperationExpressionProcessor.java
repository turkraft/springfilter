package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.LessThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
class LessThanOrEqualOperationExpressionProcessor implements
    FilterInfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<LessThanOrEqualOperator> getDefinitionType() {
    return LessThanOrEqualOperator.class;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    Expression<Comparable> left = (Expression<Comparable>) transformer.transform(source.getLeft());
    transformer.registerTargetType(source.getRight(), left.getJavaType());
    Expression<Comparable> right = (Expression<Comparable>) transformer.transform(
        source.getRight());
    return transformer.getCriteriaBuilder().lessThanOrEqualTo(left, right);
  }

}
