package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.language.LocalTimeFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
public class LocalTimeFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<LocalTimeFunction> getDefinitionType() {
    return LocalTimeFunction.class;
  }

  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      FunctionNode source) {
    return transformer.getCriteriaBuilder().localTime();
  }

}
