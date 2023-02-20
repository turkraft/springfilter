package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.helper.JsonNodeHelper;
import com.turkraft.springfilter.language.AndOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
class AndOperationJsonNodeProcessor extends InfixOperationJsonNodeProcessor {

  AndOperationJsonNodeProcessor(JsonNodeHelper jsonNodeHelper) {
    super(jsonNodeHelper);
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<AndOperator> getDefinitionType() {
    return AndOperator.class;
  }

  @Override
  public String getMongoOperator() {
    return "$and";
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    transformer.registerTargetType(source.getLeft(), Boolean.class);
    transformer.registerTargetType(source.getRight(), Boolean.class);
    return super.process(transformer, source);
  }

}
