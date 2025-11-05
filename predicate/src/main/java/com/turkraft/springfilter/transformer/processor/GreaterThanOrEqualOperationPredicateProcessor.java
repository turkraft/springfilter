package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.GreaterThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class GreaterThanOrEqualOperationPredicateProcessor implements
    FilterInfixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<GreaterThanOrEqualOperator> getDefinitionType() {
    return GreaterThanOrEqualOperator.class;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      InfixOperationNode source) {

    Predicate<?> left = transformer.transform(source.getLeft());
    Predicate<?> right = transformer.transform(source.getRight());

    return entity -> {
      Object leftValue = PredicateValueExtractor.extractValue(left, entity);
      Object rightValue = PredicateValueExtractor.extractValue(right, entity);

      if (leftValue == null || rightValue == null) {
        return false;
      }

      if (leftValue instanceof Comparable && rightValue instanceof Comparable) {
        return ((Comparable) leftValue).compareTo(rightValue) >= 0;
      }

      throw new IllegalStateException("Cannot compare non-comparable values");
    };

  }

}
