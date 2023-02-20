package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.helper.JsonNodeHelper;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public abstract class InfixOperationJsonNodeProcessor implements
    FilterInfixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {

  private final JsonNodeHelper jsonNodeHelper;

  InfixOperationJsonNodeProcessor(JsonNodeHelper jsonNodeHelper) {
    this.jsonNodeHelper = jsonNodeHelper;
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  public abstract String getMongoOperator();

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer, InfixOperationNode source) {
    return jsonNodeHelper.transform(transformer, source, getMongoOperator());
  }

}
