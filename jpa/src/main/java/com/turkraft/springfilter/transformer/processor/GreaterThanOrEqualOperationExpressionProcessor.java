package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.GreaterThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
public class GreaterThanOrEqualOperationExpressionProcessor implements
    FilterInfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<GreaterThanOrEqualOperator> getDefinitionType() {
    return GreaterThanOrEqualOperator.class;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    Expression<Comparable> left = (Expression<Comparable>) transformer.transform(source.getLeft());
    transformer.registerTargetType(source.getRight(), left.getJavaType());
    Expression<Comparable> right = (Expression<Comparable>) transformer.transform(
        source.getRight());
    return transformer.getCriteriaBuilder().greaterThanOrEqualTo(left, right);
  }

}
