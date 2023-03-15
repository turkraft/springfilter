package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.definition.FilterFunction;
import com.turkraft.springfilter.helper.ExistsExpressionHelper;
import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.language.ExistsFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@IgnoreExists
@Component
public class ExistsFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  protected final ExistsExpressionHelper existsExpressionHelper;

  ExistsFunctionExpressionProcessor(
      @Lazy ExistsExpressionHelper existsExpressionHelper) {
    this.existsExpressionHelper = existsExpressionHelper;
  }

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<? extends FilterFunction> getDefinitionType() {
    return ExistsFunction.class;
  }

  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, FunctionNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return existsExpressionHelper.wrapWithExists(transformer,
        source.getArgument(0));
  }

}
