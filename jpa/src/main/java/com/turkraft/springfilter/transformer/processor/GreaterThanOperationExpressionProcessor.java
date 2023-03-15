package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.GreaterThanOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
public class GreaterThanOperationExpressionProcessor implements
    FilterInfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<GreaterThanOperator> getDefinitionType() {
    return GreaterThanOperator.class;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    Expression<Comparable> left = (Expression<Comparable>) transformer.transform(source.getLeft());
    transformer.registerTargetType(source.getRight(), left.getJavaType());
    Expression<Comparable> right = (Expression<Comparable>) transformer.transform(
        source.getRight());
    return transformer.getCriteriaBuilder().greaterThan(left, right);
  }

}
