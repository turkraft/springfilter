package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.language.HelloWorldPlaceholder;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldPlaceholderJsonNodeProcessor implements
    FilterPlaceholderProcessor<FilterJsonNodeTransformer, JsonNode> {

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<HelloWorldPlaceholder> getDefinitionType() {
    return HelloWorldPlaceholder.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer, PlaceholderNode source) {
    transformer.registerTargetType(source, String.class);
    return transformer.getObjectMapper().createObjectNode().textNode("Hello world!");
  }

}
