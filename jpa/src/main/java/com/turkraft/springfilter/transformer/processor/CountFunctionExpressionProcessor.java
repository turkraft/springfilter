package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.language.CountFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@IgnoreExists
@Component
class CountFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  private final SizeFunctionExpressionProcessor sizeFunctionExpressionProcessor;

  public CountFunctionExpressionProcessor(
      SizeFunctionExpressionProcessor sizeFunctionExpressionProcessor) {
    this.sizeFunctionExpressionProcessor = sizeFunctionExpressionProcessor;
  }

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<CountFunction> getDefinitionType() {
    return CountFunction.class;
  }

  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      FunctionNode source) {
    return sizeFunctionExpressionProcessor.process(transformer, source);
  }

}
