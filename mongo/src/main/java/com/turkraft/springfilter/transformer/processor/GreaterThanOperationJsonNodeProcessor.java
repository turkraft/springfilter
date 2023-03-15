package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.helper.JsonNodeHelper;
import com.turkraft.springfilter.language.GreaterThanOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public class GreaterThanOperationJsonNodeProcessor extends InfixOperationJsonNodeProcessor {

  public GreaterThanOperationJsonNodeProcessor(JsonNodeHelper jsonNodeHelper) {
    super(jsonNodeHelper);
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<GreaterThanOperator> getDefinitionType() {
    return GreaterThanOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$gt";
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return super.process(transformer, source);
  }

}
