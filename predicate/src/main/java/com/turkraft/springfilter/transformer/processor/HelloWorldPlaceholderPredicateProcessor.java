package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.HelloWorldPlaceholder;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldPlaceholderPredicateProcessor implements
    FilterPlaceholderProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<HelloWorldPlaceholder> getDefinitionType() {
    return HelloWorldPlaceholder.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer, PlaceholderNode source) {
    return new ContainerPredicate<>("Hello world!");
  }

}
