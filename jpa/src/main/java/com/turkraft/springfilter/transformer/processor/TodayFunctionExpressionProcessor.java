package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.TodayFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
class TodayFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<TodayFunction> getDefinitionType() {
    return TodayFunction.class;
  }

  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, FunctionNode source) {
    transformer.registerTargetType(source, String.class);
    return transformer.getCriteriaBuilder()
        .literal(new SimpleDateFormat("EEEE").format(new Date()));
  }

}
