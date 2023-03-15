package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.helper.JsonNodeHelper;
import com.turkraft.springfilter.language.OrOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public class OrOperationJsonNodeProcessor extends InfixOperationJsonNodeProcessor {

  OrOperationJsonNodeProcessor(JsonNodeHelper jsonNodeHelper) {
    super(jsonNodeHelper);
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<OrOperator> getDefinitionType() {
    return OrOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$or";
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    transformer.registerTargetType(source.getLeft(), Boolean.class);
    transformer.registerTargetType(source.getRight(), Boolean.class);
    return super.process(transformer, source);
  }

}
