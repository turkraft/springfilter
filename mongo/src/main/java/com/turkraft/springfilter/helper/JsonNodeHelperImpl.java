package com.turkraft.springfilter.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Service;

@Service
class JsonNodeHelperImpl implements JsonNodeHelper {

  private final ObjectMapper objectMapper;

  private final FieldTypeResolver fieldTypeResolver;

  JsonNodeHelperImpl(ObjectMapper objectMapper,
      FieldTypeResolver fieldTypeResolver) {
    this.objectMapper = objectMapper;
    this.fieldTypeResolver = fieldTypeResolver;
  }

  @Override
  public ObjectNode wrapWithMongoExpression(JsonNode node) {
    return objectMapper.createObjectNode().set("$expr", node);
  }

  @Override
  public JsonNode transform(FilterJsonNodeTransformer transformer, InfixOperationNode source,
      String mongoOperator) {

    if (source.getLeft() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getRight(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    } else if (source.getRight() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getLeft(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    }

    JsonNode leftResult = transformer.transform(source.getLeft());

    transformer.registerTargetType(source.getRight(),
        transformer.getRegisteredTargetType(source.getLeft()));

    JsonNode rightResult = transformer.transform(source.getRight());

    return transformer.getObjectMapper().createObjectNode().set(mongoOperator,
        transformer.getObjectMapper().createArrayNode()
            .add(leftResult)
            .add(rightResult));

  }

}
