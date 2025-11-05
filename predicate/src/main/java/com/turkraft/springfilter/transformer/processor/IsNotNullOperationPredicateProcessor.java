package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.IsNotNullOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class IsNotNullOperationPredicateProcessor implements
    FilterPostfixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<IsNotNullOperator> getDefinitionType() {
    return IsNotNullOperator.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      PostfixOperationNode source) {

    Predicate<?> left = transformer.transform(source.getLeft());

    return entity -> PredicateValueExtractor.extractValue(left, entity) != null;

  }

}
