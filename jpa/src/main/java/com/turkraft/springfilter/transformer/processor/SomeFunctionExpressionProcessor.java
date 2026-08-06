package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.definition.FilterFunction;
import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.helper.RootContext;
import com.turkraft.springfilter.language.SomeFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.stereotype.Component;

@IgnoreExists
@Component
public class SomeFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<? extends FilterFunction> getDefinitionType() {
    return SomeFunction.class;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, FunctionNode source) {

    Subquery<Comparable> subquery = transformer
        .getCriteriaQuery()
        .subquery(Comparable.class);

    Root<?> root = subquery.correlate(transformer.getRoot());

    transformer.registerRootContext(source, new RootContext(root));

    Expression<?> expression = transformer.transform(source.getArgument(0));

    subquery.select((Expression) expression);

    return transformer
        .getCriteriaBuilder()
        .any(subquery);
  }

}
