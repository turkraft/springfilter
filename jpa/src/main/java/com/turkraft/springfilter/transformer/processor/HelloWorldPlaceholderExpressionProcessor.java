package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.HelloWorldPlaceholder;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
class HelloWorldPlaceholderExpressionProcessor implements
    FilterPlaceholderProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<HelloWorldPlaceholder> getDefinitionType() {
    return HelloWorldPlaceholder.class;
  }

  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, PlaceholderNode source) {
    transformer.registerTargetType(source, String.class);
    return transformer.getCriteriaBuilder().literal("Hello world!");
  }

}
