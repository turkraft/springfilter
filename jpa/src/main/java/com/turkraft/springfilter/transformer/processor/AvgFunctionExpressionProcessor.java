package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.helper.RootContext;
import com.turkraft.springfilter.language.AvgFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.stereotype.Component;

@IgnoreExists
@Component
class AvgFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<AvgFunction> getDefinitionType() {
    return AvgFunction.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      FunctionNode source) {

    Subquery<Double> subquery = transformer.getCriteriaQuery().subquery(Double.class);

    Root<?> root = subquery.correlate(transformer.getRoot());

    transformer.registerRootContext(source, new RootContext(root));

    subquery.select(
        transformer.getCriteriaBuilder().avg((Expression<Number>) transformer.transform(
            source.getArgument(0))));

    return subquery;

  }

}
