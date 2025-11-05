package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.AndOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class AndOperationPredicateProcessor implements
    FilterInfixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<AndOperator> getDefinitionType() {
    return AndOperator.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      InfixOperationNode source) {

    Predicate<Object> left = transformer.transform(source.getLeft());
    Predicate<Object> right = transformer.transform(source.getRight());

    return left.and(right);

  }

}
