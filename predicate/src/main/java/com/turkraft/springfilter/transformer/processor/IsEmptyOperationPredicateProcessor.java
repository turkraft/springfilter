package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.IsEmptyOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class IsEmptyOperationPredicateProcessor implements
    FilterPostfixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<IsEmptyOperator> getDefinitionType() {
    return IsEmptyOperator.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      PostfixOperationNode source) {

    Predicate<?> left = transformer.transform(source.getLeft());

    return entity -> {
      Object value = PredicateValueExtractor.extractValue(left, entity);

      if (value == null) {
        return true;
      }

      if (value instanceof String) {
        return ((String) value).isEmpty();
      }

      if (value instanceof Collection) {
        return ((Collection<?>) value).isEmpty();
      }

      if (value instanceof Map) {
        return ((Map<?, ?>) value).isEmpty();
      }

      if (value.getClass().isArray()) {
        return ((Object[]) value).length == 0;
      }

      return false;
    };

  }

}
