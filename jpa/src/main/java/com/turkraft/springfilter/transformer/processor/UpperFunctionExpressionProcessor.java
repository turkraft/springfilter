package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.UpperFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
public class UpperFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<UpperFunction> getDefinitionType() {
    return UpperFunction.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      FunctionNode source) {
    transformer.registerTargetType(source.getArgument(0), String.class);
    return transformer.getCriteriaBuilder()
        .upper((Expression<String>) transformer.transform(source.getArgument(0)));
  }

}
