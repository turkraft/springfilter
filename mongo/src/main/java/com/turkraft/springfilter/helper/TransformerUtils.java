package com.turkraft.springfilter.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;

public interface TransformerUtils {
    JsonNode wrapArrays(FilterJsonNodeTransformer transformer, JsonNode node, InfixOperationNode source);

    JsonNode wrapArraysRegex(FilterJsonNodeTransformer transformer, JsonNode node, InfixOperationNode source);

    JsonNode wrapArrays(FilterJsonNodeTransformer transformer, JsonNode node, InfixOperationNode source, String mongoOperator);
}
