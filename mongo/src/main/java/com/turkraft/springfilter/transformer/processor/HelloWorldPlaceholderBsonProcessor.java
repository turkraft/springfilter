package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.HelloWorldPlaceholder;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldPlaceholderBsonProcessor implements
    FilterPlaceholderProcessor<FilterBsonTransformer, Object> {

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<HelloWorldPlaceholder> getDefinitionType() {
    return HelloWorldPlaceholder.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer, PlaceholderNode source) {
    transformer.registerTargetType(source, String.class);
    return "Hello world!";
  }

}
