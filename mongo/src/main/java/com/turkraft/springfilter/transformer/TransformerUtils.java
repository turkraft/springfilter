package com.turkraft.springfilter.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;

public interface TransformerUtils {
    JsonNode wrapArrays(FilterJsonNodeTransformer transformer, JsonNode node, InfixOperationNode source);

    JsonNode wrapArraysRegex(FilterJsonNodeTransformer transformer, JsonNode node, InfixOperationNode source);

    JsonNode wrapArrays(FilterJsonNodeTransformer transformer, JsonNode node, InfixOperationNode source, String mongoOperator);

    JsonNode simplify(FilterJsonNodeTransformer transformer, JsonNode node);
}
