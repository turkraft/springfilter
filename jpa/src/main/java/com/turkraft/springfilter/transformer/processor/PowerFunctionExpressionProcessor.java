package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.PowerFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
class PowerFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<PowerFunction> getDefinitionType() {
    return PowerFunction.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      FunctionNode source) {
    transformer.registerTargetType(source.getArgument(0), Number.class);
    transformer.registerTargetType(source.getArgument(1), Number.class);
    return transformer.getCriteriaBuilder()
        .power((Expression<Number>) transformer.transform(source.getArgument(0)),
            (Expression<Number>) transformer.transform(source.getArgument(1)));
  }

}
