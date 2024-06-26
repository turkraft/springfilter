package com.turkraft.springfilter.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import com.turkraft.springfilter.transformer.TransformerUtils;
import org.springframework.stereotype.Service;

@Service
public class JsonNodeHelperImpl implements JsonNodeHelper {

  protected final ObjectMapper objectMapper;
  protected final TransformerUtils transformerUtils;
  protected final FieldTypeResolver fieldTypeResolver;

  public JsonNodeHelperImpl(ObjectMapper objectMapper,
                            FieldTypeResolver fieldTypeResolver,
                            TransformerUtils transformerUtils) {
    this.objectMapper = objectMapper;
    this.fieldTypeResolver = fieldTypeResolver;
    this.transformerUtils = transformerUtils;
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

    if (transformer.getRegisteredTargetType(source.getLeft()) != null) {
      transformer.registerTargetType(source.getRight(),
              transformer.getRegisteredTargetType(source.getLeft()));
    } else if (transformer.getRegisteredTargetType(source.getRight()) != null) {
      transformer.registerTargetType(source.getLeft(),
              transformer.getRegisteredTargetType(source.getRight()));
    }

    JsonNode rightResult = transformer.transform(source.getRight());

    JsonNode result = transformer.getObjectMapper().createObjectNode().set(mongoOperator,
            transformer.getObjectMapper().createArrayNode()
                    .add(leftResult)
                    .add(rightResult));

    return transformerUtils.wrapArrays(transformer, result, source, mongoOperator);
  }

}
