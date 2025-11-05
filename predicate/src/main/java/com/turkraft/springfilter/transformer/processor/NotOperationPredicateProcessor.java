package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.NotOperator;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class NotOperationPredicateProcessor implements
    FilterPrefixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<NotOperator> getDefinitionType() {
    return NotOperator.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      PrefixOperationNode source) {

    Predicate<Object> right = transformer.transform(source.getRight());

    return right.negate();

  }

}
