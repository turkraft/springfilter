package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.XorOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
public class XorOperationExpressionProcessor implements
    FilterInfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<XorOperator> getDefinitionType() {
    return XorOperator.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    transformer.registerTargetType(source.getLeft(), Boolean.class);
    transformer.registerTargetType(source.getRight(), Boolean.class);
    CriteriaBuilder cb = transformer.getCriteriaBuilder();
    Expression<Boolean> left = (Expression<Boolean>) transformer.transform(source.getLeft());
    Expression<Boolean> right = (Expression<Boolean>) transformer.transform(source.getRight());
    return cb.or(
        cb.and(left, cb.not(right)),
        cb.and(cb.not(left), right));
  }

}
