package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.NotOperator;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
public class NotOperationExpressionProcessor implements
    FilterPrefixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<NotOperator> getDefinitionType() {
    return NotOperator.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      PrefixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    transformer.registerTargetType(source.getRight(), Boolean.class);
    return transformer.getCriteriaBuilder()
        .not((Expression<Boolean>) transformer.transform(source.getRight()));
  }

}
