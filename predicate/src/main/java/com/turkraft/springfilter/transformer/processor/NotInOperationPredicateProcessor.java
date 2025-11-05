package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.NotInOperator;
import com.turkraft.springfilter.parser.node.CollectionNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class NotInOperationPredicateProcessor implements
    FilterInfixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<NotInOperator> getDefinitionType() {
    return NotInOperator.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      InfixOperationNode source) {

    Predicate<?> left = transformer.transform(source.getLeft());

    if (source.getRight() instanceof CollectionNode collectionNode) {

      List<Predicate<?>> items = new ArrayList<>();

      for (FilterNode item : collectionNode.getItems()) {
        items.add(transformer.transform(item));
      }

      return entity -> {
        Object leftValue = PredicateValueExtractor.extractValue(left, entity);

        List<Object> values = new ArrayList<>();

        for (Predicate<?> itemPredicate : items) {
          values.add(PredicateValueExtractor.extractValue(itemPredicate, entity));
        }

        return values.stream().noneMatch(v -> Objects.equals(leftValue, v));
      };

    }

    Predicate<?> right = transformer.transform(source.getRight());

    return entity -> {
      Object leftValue = PredicateValueExtractor.extractValue(left, entity);
      Object rightValue = PredicateValueExtractor.extractValue(right, entity);

      if (rightValue instanceof Collection) {
        return ((Collection<?>) rightValue).stream()
            .noneMatch(v -> Objects.equals(leftValue, v));
      }

      if (rightValue != null && rightValue.getClass().isArray()) {
        for (Object item : (Object[]) rightValue) {
          if (Objects.equals(leftValue, item)) {
            return false;
          }
        }
        return true;
      }

      return !Objects.equals(leftValue, rightValue);
    };

  }

}
