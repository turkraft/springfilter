package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.ExistsExpressionHelper;
import com.turkraft.springfilter.helper.IgnoreExists;
import com.turkraft.springfilter.language.IsNotEmptyOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@IgnoreExists
@Component
public class IsNotEmptyOperationExpressionProcessor implements
    FilterPostfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  private final ExistsExpressionHelper existsExpressionHelper;

  IsNotEmptyOperationExpressionProcessor(@Lazy ExistsExpressionHelper existsExpressionHelper) {
    this.existsExpressionHelper = existsExpressionHelper;
  }

  @Override

  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<IsNotEmptyOperator> getDefinitionType() {
    return IsNotEmptyOperator.class;
  }

  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      PostfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return existsExpressionHelper.wrapWithExists(transformer, source.getLeft()
    );
  }

}
