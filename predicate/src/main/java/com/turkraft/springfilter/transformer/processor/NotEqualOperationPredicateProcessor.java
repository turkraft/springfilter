package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.NotEqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.Objects;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class NotEqualOperationPredicateProcessor implements
    FilterInfixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<NotEqualOperator> getDefinitionType() {
    return NotEqualOperator.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      InfixOperationNode source) {

    Predicate<?> left = transformer.transform(source.getLeft());
    Predicate<?> right = transformer.transform(source.getRight());

    return entity -> {
      Object leftValue = PredicateValueExtractor.extractValue(left, entity);
      Object rightValue = PredicateValueExtractor.extractValue(right, entity);
      return !Objects.equals(leftValue, rightValue);
    };

  }

}
