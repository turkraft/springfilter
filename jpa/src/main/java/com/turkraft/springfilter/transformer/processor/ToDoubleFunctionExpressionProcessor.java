package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.language.ToDoubleFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
class ToDoubleFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<ToDoubleFunction> getDefinitionType() {
    return ToDoubleFunction.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      FunctionNode source) {
    transformer.registerTargetType(source.getArgument(0), Number.class);
    return transformer.getCriteriaBuilder()
        .toDouble((Expression<Number>) transformer.transform(source.getArgument(0)));
  }

}
