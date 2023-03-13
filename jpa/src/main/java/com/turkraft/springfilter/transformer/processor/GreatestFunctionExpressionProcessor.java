package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.helper.RootContext;
import com.turkraft.springfilter.language.GreatestFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.stereotype.Component;

@IgnoreExists
@Component
class GreatestFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<GreatestFunction> getDefinitionType() {
    return GreatestFunction.class;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      FunctionNode source) {

    Subquery<Comparable> subquery = transformer.getCriteriaQuery().subquery(Comparable.class);

    Root<?> root = subquery.correlate(transformer.getRoot());

    transformer.registerRootContext(source, new RootContext(root));

    subquery.select(
        transformer.getCriteriaBuilder().greatest((Expression<Comparable>) transformer.transform(
            source.getArgument(0))));

    return subquery;

  }

}
