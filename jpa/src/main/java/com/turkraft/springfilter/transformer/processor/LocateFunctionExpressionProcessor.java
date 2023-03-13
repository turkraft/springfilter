package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.language.LocateFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
class LocateFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<LocateFunction> getDefinitionType() {
    return LocateFunction.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      FunctionNode source) {

    transformer.registerTargetType(source.getArgument(0), String.class);
    transformer.registerTargetType(source.getArgument(1), String.class);

    if (source.getArguments().size() >= 2) {
      transformer.registerTargetType(source.getArgument(2), Integer.class);
      return transformer.getCriteriaBuilder()
          .locate((Expression<String>) transformer.transform(source.getArgument(0)),
              (Expression<String>) transformer.transform(source.getArgument(1)),
              (Expression<Integer>) transformer.transform(source.getArgument(2)));
    }

    return transformer.getCriteriaBuilder()
        .locate((Expression<String>) transformer.transform(source.getArgument(0)),
            (Expression<String>) transformer.transform(source.getArgument(1)));
  }

}
