package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.helper.JsonNodeHelper;
import com.turkraft.springfilter.language.EqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public class EqualOperationJsonNodeProcessor extends InfixOperationJsonNodeProcessor {

  public EqualOperationJsonNodeProcessor(JsonNodeHelper jsonNodeHelper) {
    super(jsonNodeHelper);
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<EqualOperator> getDefinitionType() {
    return EqualOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$eq";
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return super.process(transformer, source);
  }

}
