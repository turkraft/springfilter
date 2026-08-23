package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.JsonNodeHelper;
import com.turkraft.springfilter.language.GreaterThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class GreaterThanOrEqualOperationJsonNodeProcessor extends InfixOperationJsonNodeProcessor {

  public GreaterThanOrEqualOperationJsonNodeProcessor(JsonNodeHelper jsonNodeHelper) {
    super(jsonNodeHelper);
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<GreaterThanOrEqualOperator> getDefinitionType() {
    return GreaterThanOrEqualOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$gte";
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    return super.process(transformer, source);
  }

}
