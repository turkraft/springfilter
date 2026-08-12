package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.JsonTextFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
public class JsonTextFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<JsonTextFunction> getDefinitionType() {
    return JsonTextFunction.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      FunctionNode source) {

    Expression<?>[] args = new Expression<?>[source.getArguments().size()];
    args[0] = transformer.transform(source.getArgument(0));

    for (int i = 1; i < source.getArguments().size(); i++) {
      transformer.registerTargetType(source.getArgument(i), String.class);
      args[i] = transformer.transform(source.getArgument(i));
    }

    return transformer
        .getCriteriaBuilder()
        .function("jsonb_extract_path_text", String.class, args);
  }

}
