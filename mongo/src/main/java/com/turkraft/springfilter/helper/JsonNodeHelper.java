package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

public interface JsonNodeHelper {

  ObjectNode wrapWithMongoExpression(JsonNode node);

  JsonNode transform(FilterJsonNodeTransformer transformer, InfixOperationNode source,
      String mongoOperator);

}
