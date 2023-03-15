package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.helper.RootContext;
import com.turkraft.springfilter.language.NotInOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.stereotype.Component;

@IgnoreExists
@Component
public class NotInOperationExpressionProcessor implements
    FilterInfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  private final InOperationExpressionProcessor inOperationExpressionProcessor;

  public NotInOperationExpressionProcessor(
      InOperationExpressionProcessor inOperationExpressionProcessor) {
    this.inOperationExpressionProcessor = inOperationExpressionProcessor;
  }

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<NotInOperator> getDefinitionType() {
    return NotInOperator.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      InfixOperationNode source) {

    transformer.registerTargetType(source, Boolean.class);

    Subquery<Integer> subquery = transformer.getCriteriaQuery().subquery(Integer.class);

    Root<?> subroot = subquery.correlate(transformer.getRoot());

    transformer.registerRootContext(source, new RootContext(subroot));

    subquery.select(transformer.getCriteriaBuilder().literal(1));

    Expression<Boolean> expression = (Expression<Boolean>) inOperationExpressionProcessor.process(
        transformer, source);

    subquery.where(expression);

    return transformer.getCriteriaBuilder().exists(subquery).not();

//    return transformer.getCriteriaBuilder()
//        .not((Expression<Boolean>) inOperationExpressionProcessor.process(transformer, source));
  }

}
