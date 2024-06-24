package com.turkraft.springfilter.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class TransformerUtilsImpl implements TransformerUtils{

    private final FieldTypeResolver fieldTypeResolver;

    TransformerUtilsImpl(FieldTypeResolver fieldTypeResolver) {
        this.fieldTypeResolver = fieldTypeResolver;
    }


    public JsonNode wrapArrays(FilterJsonNodeTransformer transformer, JsonNode node, InfixOperationNode source) {
        if (!(source.getLeft() instanceof FieldNode)) {
            return node;
        }

        String textValue = transformer.transform(source.getLeft()).textValue();
        String[] fields = textValue.replace("$", "").split("\\.");
        List<String> arrayPaths = getArrayPaths(transformer, source, fields);

        if (arrayPaths.isEmpty()) {
            return node;
        }

        JsonNode resultNode = null;

        for (int i = arrayPaths.size() - 1; i >= 0; i--) {
            String input = (i > 0) ? "$$this.".concat(arrayPaths.get(i)) : "$".concat(arrayPaths.get(i));

            if (resultNode == null) {
                FilterNode left = getFilterNode(transformer, source, arrayPaths, i);
                JsonNode newNode = transformer.getObjectMapper().createObjectNode().set("$and",
                        transformer.getObjectMapper().createArrayNode()
                                .add(transformer.getObjectMapper().createObjectNode()
                                        .set("$isArray", transformer.getObjectMapper().createArrayNode()
                                                .add(transformer.transform(source.getRight()))))
                                .add(transformer.getObjectMapper().createObjectNode().set("$in",
                                        transformer.getObjectMapper().createArrayNode()
                                                .add(transformer.transform(left))
                                                .add(transformer.transform(source.getRight())))));

                resultNode = getJsonNode(transformer, input, newNode);
            } else {
                JsonNode ifNull = getIfNullNode(transformer, input);

                resultNode = getMapNode(transformer, ifNull, resultNode);
            }

        }
        return resultNode;
    }

    private String getLeafNodePath(String[] fields, List<String> arrayPaths) {
        StringBuilder fullPath = new StringBuilder();
        for (int i = fields.length-1; i >=0 ; i--) {
            if(arrayPaths.contains(fields[i])){
                for (int j = i+1; j < fields.length; j++) {
                    if (fullPath.isEmpty()) {
                        fullPath.append(fields[j]);
                    } else {
                        fullPath.append(".").append(fields[j]);
                    }
                }
                break;
            }
        }
    return fullPath.toString();
    }

    public JsonNode wrapArraysRegex(FilterJsonNodeTransformer transformer, JsonNode node, InfixOperationNode source) {
        if (!(source.getLeft() instanceof FieldNode)) {
            return node;
        }
        String textValue = transformer.transform(source.getLeft()).textValue();
        String[] fields = textValue.replace("$", "").split("\\.");

        List<String> arrayPaths = getArrayPaths(transformer, source, fields);

        if (arrayPaths.isEmpty()) {
            return node;
        }

        JsonNode resultNode = null;
        for (int i = arrayPaths.size() - 1; i >= 0; i--) {
            String input = (i > 0) ? "$$this.".concat(arrayPaths.get(i)) : "$".concat(arrayPaths.get(i));

            if (resultNode == null) {
                List<String> split = new ArrayList<>(List.of(transformer.transform(source.getLeft()).asText()
                        .split(arrayPaths.get(i), 2)));

                if (split.size() == 1) {
                    split.add("");
                }

                JsonNode regex = node.findValue("$regexMatch");
                ObjectNode regexPayload = (ObjectNode) regex;

                regexPayload.put("input", "$$this."+getLeafNodePath(fields, arrayPaths));

                resultNode = getJsonNode(transformer, input, node);
            } else {
                JsonNode ifNull = getIfNullNode(transformer, input);

                resultNode = getMapNode(transformer, ifNull, resultNode);
            }

        }
        return resultNode;
    }

    private static JsonNode getMapNode(FilterJsonNodeTransformer transformer, JsonNode ifNull, JsonNode resultNode) {
        return transformer.getObjectMapper().createObjectNode().set("$anyElementTrue",
                transformer.getObjectMapper().createObjectNode().set("$map",
                        transformer.getObjectMapper().createObjectNode()
                                .putPOJO("input", ifNull)
                                .set("in", resultNode)));
    }

    public JsonNode wrapArrays(FilterJsonNodeTransformer transformer, JsonNode node, InfixOperationNode source, String mongoOperator) {
        if (!(source.getLeft() instanceof FieldNode)) {
            return node;
        }
        String textValue = transformer.transform(source.getLeft()).textValue();
        String[] fields = textValue.replace("$", "").split("\\.");
        List<String> arrayPaths = getArrayPaths(transformer, source, fields);

        if (arrayPaths.isEmpty()) {
            return node;
        }

        JsonNode resultNode = null;

        for (int i = arrayPaths.size() - 1; i >= 0; i--) {
            String input = (i > 0) ? "$$this.".concat(arrayPaths.get(i)) : "$".concat(arrayPaths.get(i));

            if (resultNode == null) {
                FilterNode left = getFilterNode(transformer, source, arrayPaths, i);

                JsonNode newNode = transformer.getObjectMapper().createObjectNode().set(mongoOperator,
                        transformer.getObjectMapper().createArrayNode()
                                .add(transformer.transform(left))
                                .add(transformer.transform(source.getRight())));

                resultNode = getJsonNode(transformer, input, newNode);
            } else {
                JsonNode ifNull = getIfNullNode(transformer, input);

                resultNode = getMapNode(transformer, ifNull, resultNode);
            }

        }
        return resultNode;
    }

    private static FilterNode getFilterNode(FilterJsonNodeTransformer transformer, InfixOperationNode source, List<String> arrayPaths, int i) {
        List<String> split = new ArrayList<>(List.of(transformer.transform(source.getLeft()).asText().split(arrayPaths.get(i), 2)));

        if (split.size() == 1) {
            split.add("");
        }

        String nodeName = "$this".concat(split.get(1));
        FilterNode left = new FieldNode(nodeName);
        left.setPayload(source.getLeft().getPayload());
        return left;
    }

    private List<String> getArrayPaths(FilterJsonNodeTransformer transformer, InfixOperationNode source, String[] fields) {
        List<String> arrayPaths = new ArrayList<>();

        String fullPath = "";
        String prevArrayPath = "";

        for (String field : fields) {

            if (fullPath.isEmpty()) {
                fullPath = fullPath.concat(field);
            } else {
                fullPath = fullPath.concat(".").concat(field);
            }

            boolean isArray = fieldTypeResolver.isIterable(transformer.getEntityType(), fullPath.replace("_id", "id"));

            if (isArray) {
                String result = fullPath.replace(prevArrayPath, "");

                if (!arrayPaths.isEmpty()) result = result.replaceFirst("\\.", "");

                arrayPaths.add(result);
                prevArrayPath = fullPath;
            }
        }
        return arrayPaths;
    }

    private JsonNode getJsonNode(FilterJsonNodeTransformer transformer, String input, JsonNode newNode) {
        JsonNode resultNode;
        JsonNode ifNull = getIfNullNode(transformer, input);

        resultNode = transformer.getObjectMapper().createObjectNode().set("$anyElementTrue",
                transformer.getObjectMapper().createObjectNode().set("$map",
                        transformer.getObjectMapper().createObjectNode()
                                .putPOJO("input", ifNull)
                                .put("as", "this")
                                .set("in", newNode)));
        return resultNode;
    }

    private static JsonNode getIfNullNode(FilterJsonNodeTransformer transformer, String input) {
        return transformer.getObjectMapper().createObjectNode().set("$ifNull",
                transformer.getObjectMapper().createArrayNode().add(input).add(transformer.getObjectMapper().createArrayNode()));
    }
}
