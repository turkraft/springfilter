package com.turkraft.springfilter.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;

public interface JsonNodeHelper {

  ObjectNode wrapWithMongoExpression(JsonNode node);

  JsonNode transform(FilterJsonNodeTransformer transformer, InfixOperationNode source,
      String mongoOperator);

}
