package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.OrOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class OrOperationPredicateProcessor implements
    FilterInfixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<OrOperator> getDefinitionType() {
    return OrOperator.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      InfixOperationNode source) {

    Predicate<Object> left = transformer.transform(source.getLeft());
    Predicate<Object> right = transformer.transform(source.getRight());

    return left.or(right);

  }

}
